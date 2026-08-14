#!/bin/bash
# EC2 user-data for the Jenkins CONTROLLER. Amazon Linux 2023, t3.small or larger.
#
# Paste into "Advanced details -> User data" at launch, or run by hand on a
# running box with sudo. Safe to re-run: every step is idempotent.
#
# This installs the runtime and lays down the compose project. It does NOT start
# Jenkins -- JENKINS_URL has to be set in .env first, and on a migration the
# JENKINS_HOME volume has to be restored before the first boot, or Jenkins
# initialises an empty home and you get the setup wizard instead of your jobs.
# Restore with infra/migrate-jenkins-home.sh import, then `docker compose up -d`.
set -euo pipefail

exec > >(tee /var/log/jenkins-bootstrap.log) 2>&1
echo "=== controller bootstrap $(date -Is) ==="

dnf update -y
dnf install -y docker git tar

systemctl enable --now docker
usermod -aG docker ec2-user

# The compose plugin is not in the AL2023 repos; this is the documented install.
COMPOSE_DIR=/usr/local/lib/docker/cli-plugins
mkdir -p "$COMPOSE_DIR"
curl -fsSL \
  "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-$(uname -m)" \
  -o "$COMPOSE_DIR/docker-compose"
chmod +x "$COMPOSE_DIR/docker-compose"

install -d -o ec2-user -g ec2-user /home/ec2-user/jenkins

# Docker's default log driver keeps growing until the root volume is full, and
# a Jenkins controller that cannot write to disk fails in confusing ways.
cat > /etc/docker/daemon.json <<'EOF'
{
  "log-driver": "json-file",
  "log-opts": { "max-size": "10m", "max-file": "3" }
}
EOF
systemctl restart docker

echo "=== bootstrap complete ==="
echo "Next: copy infra/controller/{compose.yaml,Caddyfile} to ~/jenkins,"
echo "write ~/jenkins/.env, restore the JENKINS_HOME volume, then 'docker compose up -d'."
