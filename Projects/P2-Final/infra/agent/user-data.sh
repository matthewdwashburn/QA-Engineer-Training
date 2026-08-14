#!/bin/bash
# EC2 user-data for the Jenkins BUILD AGENT. Amazon Linux 2023, t3.large or larger.
#
# t3.large is a floor, not a preference: a build runs Maven, a Postgres test
# container, the four-service app stack, Chrome, and JMeter at 100 threads. On
# anything smaller the JMeter numbers stop meaning anything, because the load
# generator is competing with the thing it is measuring.
#
# Installs everything the pipeline shells out to. Deliberately explicit rather
# than relying on Jenkins tool auto-install, so a failed build is never
# ambiguous about whether the toolchain or the code broke.
set -euo pipefail

exec > >(tee /var/log/jenkins-agent-bootstrap.log) 2>&1
echo "=== agent bootstrap $(date -Is) ==="

dnf update -y
# No `curl` here: AL2023 ships curl-minimal, and the full package conflicts with it.
dnf install -y git tar unzip python3 python3-pip docker

# AL2023's python3 is 3.9, but psycopg 3.3 needs >= 3.10; 3.12 matches the app images.
# Never repoint /usr/bin/python3 to this -- dnf is written in Python and would break.
CI_PYTHON=""
for v in 3.12 3.11; do
  if dnf install -y "python${v}" "python${v}-pip"; then CI_PYTHON="python${v}"; break; fi
done
[ -n "$CI_PYTHON" ] || { echo "No python >= 3.10 available in the AL2023 repos"; exit 1; }
ln -sfn "$(command -v "$CI_PYTHON")" /usr/local/bin/python3-ci

# ---------------------------------------------------------------- docker
systemctl enable --now docker
# Without this the agent cannot talk to the socket and every compose step fails.
usermod -aG docker ec2-user

COMPOSE_DIR=/usr/local/lib/docker/cli-plugins
mkdir -p "$COMPOSE_DIR"
curl -fsSL \
  "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-$(uname -m)" \
  -o "$COMPOSE_DIR/docker-compose"
chmod +x "$COMPOSE_DIR/docker-compose"

# `docker compose build` delegates to buildx and refuses to run without it.
# Release assets embed the version in the filename, so the tag has to be resolved first.
BUILDX_VER=$(curl -fsSL https://api.github.com/repos/docker/buildx/releases/latest \
  | sed -n 's/.*"tag_name": *"\([^"]*\)".*/\1/p')
[ -n "$BUILDX_VER" ] || { echo "Could not resolve the latest buildx release"; exit 1; }
# linux-amd64, not $(uname -m): buildx names it amd64 where compose says x86_64.
curl -fsSL "https://github.com/docker/buildx/releases/download/${BUILDX_VER}/buildx-${BUILDX_VER}.linux-amd64" \
  -o "$COMPOSE_DIR/docker-buildx"
chmod +x "$COMPOSE_DIR/docker-buildx"

# Builds churn images; uncapped logs and dead layers fill a 30G root volume fast.
cat > /etc/docker/daemon.json <<'EOF'
{
  "log-driver": "json-file",
  "log-opts": { "max-size": "10m", "max-file": "3" }
}
EOF
systemctl restart docker

# ---------------------------------------------------------------- JDKs
# Two of them, for two unrelated reasons:
#   21 - the controller image is lts-jdk21, so remoting ships class file 65 and
#        the agent PROCESS will not start on anything older.
#   17 - what manager-app compiles against and jlinks; the Jenkinsfile selects
#        it per build via `tools { jdk 'temurin-17' }`.
install -d /opt/java

install_temurin() {
  local ver="$1" dest="/opt/java/temurin-${1}"
  [ -d "$dest" ] && { echo "temurin-$ver already present"; return; }
  echo "Installing Temurin $ver..."
  # The Adoptium API resolves 'latest GA' itself, so this does not pin a
  # patch version that goes 404 in six months.
  curl -fsSL "https://api.adoptium.net/v3/binary/latest/${ver}/ga/linux/x64/jdk/hotspot/normal/eclipse" \
    -o "/tmp/temurin-${ver}.tar.gz"
  mkdir -p "$dest"
  tar xzf "/tmp/temurin-${ver}.tar.gz" -C "$dest" --strip-components=1
  rm -f "/tmp/temurin-${ver}.tar.gz"
}

install_temurin 21
install_temurin 17

# ---------------------------------------------------------------- maven
# archive.apache.org, not dlcdn: dlcdn drops non-current versions, so a pinned
# URL there breaks the day 3.9.9 stops being the release.
MAVEN_VERSION=3.9.9
if [ ! -d "/opt/maven-${MAVEN_VERSION}" ]; then
  curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
    -o /tmp/maven.tar.gz
  mkdir -p "/opt/maven-${MAVEN_VERSION}"
  tar xzf /tmp/maven.tar.gz -C "/opt/maven-${MAVEN_VERSION}" --strip-components=1
  rm -f /tmp/maven.tar.gz
fi
ln -sfn "/opt/maven-${MAVEN_VERSION}" /opt/maven

# Jenkins `sh` steps are non-login shells, so /etc/profile.d is never sourced for them.
ln -sfn /opt/maven/bin/mvn /usr/local/bin/mvn

# For interactive SSH sessions only; builds rely on the symlink above.
cat > /etc/profile.d/jenkins-toolchain.sh <<'EOF'
export JAVA_HOME=/opt/java/temurin-17
export M2_HOME=/opt/maven
export PATH="$JAVA_HOME/bin:$M2_HOME/bin:$PATH"
EOF
chmod 0644 /etc/profile.d/jenkins-toolchain.sh

# ---------------------------------------------------------------- chrome
# Selenium 4.45 bundles Selenium Manager, which downloads a matching
# chromedriver at runtime -- so the browser is the only thing to install here.
if ! command -v google-chrome >/dev/null 2>&1; then
  # Fonts are not a dependency Chrome declares, but without them every
  # rendered page is blank boxes and screenshots are useless.
  dnf install -y liberation-fonts
  dnf install -y https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm
fi

# Chrome's renderer wants more shared memory than Docker's 64M default. Not
# containerised here, but /dev/shm on a t3 is sized off RAM and still tight.
mount -o remount,size=2G /dev/shm || true

# ---------------------------------------------------------------- agent
install -d -o ec2-user -g ec2-user /home/ec2-user/agent

install -m 0644 /dev/stdin /etc/systemd/system/jenkins-agent.service <<'EOF'
[Unit]
Description=Jenkins inbound build agent
# Builds shell out to docker on the first step; starting before it is ready
# produces a confusing "cannot connect to the Docker daemon" on stage one.
After=network-online.target docker.service
Wants=network-online.target
Requires=docker.service

[Service]
User=ec2-user
Group=docker
WorkingDirectory=/home/ec2-user/agent
# JENKINS_URL, AGENT_NAME and AGENT_SECRET live here, 0600, so the node secret
# is not readable in `systemctl cat` or the unit file itself.
EnvironmentFile=/etc/jenkins-agent.env

# Re-fetched on every start: agent.jar must match the controller's remoting
# version, and pinning a downloaded copy breaks silently after a controller
# upgrade.
ExecStartPre=/usr/bin/curl -fsSL -o /home/ec2-user/agent/agent.jar ${JENKINS_URL}/jnlpJars/agent.jar

# -webSocket tunnels remoting over the same 443 Caddy already terminates, so
# the controller never needs port 50000 open to the internet.
ExecStart=/opt/java/temurin-21/bin/java -jar /home/ec2-user/agent/agent.jar \
    -url ${JENKINS_URL} \
    -name ${AGENT_NAME} \
    -secret ${AGENT_SECRET} \
    -webSocket \
    -workDir /home/ec2-user/agent

Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Placeholder only. Real values go in during the runbook, before enabling.
if [ ! -f /etc/jenkins-agent.env ]; then
  cat > /etc/jenkins-agent.env <<'EOF'
JENKINS_URL=https://REPLACE_ME
AGENT_NAME=linux-build
AGENT_SECRET=REPLACE_ME
EOF
  chmod 0600 /etc/jenkins-agent.env
fi

systemctl daemon-reload

# ---------------------------------------------------------------- verify
# Every binary the Jenkinsfile shells out to, checked here so a missing one
# fails in three minutes rather than five stages into a ten-minute build.
# Deliberately not a login shell: that is the environment Jenkins `sh` gets.
echo "--- verifying toolchain ---"
# Maven needs a JAVA_HOME; Jenkins supplies one per build from the jdk tool.
export JAVA_HOME=/opt/java/temurin-17
verify_failed=0
check() {
  if "$@" >/dev/null 2>&1; then
    echo "  ok    $1"
  else
    echo "  FAIL  $1"
    verify_failed=1
  fi
}

check git --version
check docker --version
check docker compose version
check docker buildx version
check mvn -v
check python3-ci --version
check google-chrome --version
check curl --version
check /opt/java/temurin-21/bin/java -version
check /opt/java/temurin-17/bin/java -version

if [ "$verify_failed" -ne 0 ]; then
  echo "=== bootstrap FAILED verification -- do not register this node ==="
  exit 1
fi

echo "=== bootstrap complete ==="
echo "Next: create the node in Jenkins, put its URL/name/secret in"
echo "/etc/jenkins-agent.env, then 'systemctl enable --now jenkins-agent'."
