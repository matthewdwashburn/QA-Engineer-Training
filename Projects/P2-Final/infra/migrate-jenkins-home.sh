#!/usr/bin/env bash
# Move JENKINS_HOME from the laptop controller to the EC2 controller.
#
# Why copy the volume instead of recreating the controller by hand: JENKINS_HOME
# holds secrets/master.key alongside credentials.xml, so carrying the whole
# directory carries all four credentials (jwt-secret, ghcr, ec2-deploy-key,
# postgres-password) already decryptable. It also brings jobs, build history,
# installed plugins, and the agent's node secret -- which is why the agent
# reconnects to the new controller with only a URL change.
#
# Usage:
#   export/import are separate so you can inspect the archive in between.
#
#   ./migrate-jenkins-home.sh export
#   ./migrate-jenkins-home.sh push  ec2-user@<controller-ip>  ~/rev_key.pem
#   ./migrate-jenkins-home.sh import                      # run ON the EC2 box
set -euo pipefail

ARCHIVE=jenkins-home.tgz
# The volume as compose names it: project 'jenkins-ci' + volume 'jenkins-home'.
SRC_VOLUME=jenkins-ci_jenkins-home
# Anchor to the script, so export/push work from any working directory.
HERE=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
REPO_ROOT=$(cd "$HERE/.." && pwd)

# Git Bash rewrites container paths (/out, /src) into C:/Program Files/Git/...
export MSYS2_ARG_CONV_EXCL='*'
# Windows-style host path for -v; 'pwd -W' is absent off Git Bash, hence the fallback.
host_path() { (cd "$1" && { pwd -W 2>/dev/null || pwd; }); }

usage() { sed -n '2,20p' "$0"; exit 1; }

case "${1:-}" in

  export)
    # Stop first: tarring a live JENKINS_HOME can catch a half-written config.
    echo "Stopping the local controller so the archive is consistent..."
    docker compose -f "$(host_path "$REPO_ROOT")/jenkins/compose.yaml" stop || true

    echo "Archiving $SRC_VOLUME -> $ARCHIVE"
    # A throwaway alpine with the volume mounted: the only way to read a named
    # volume's contents without a container that already has it.
    docker run --rm \
      -v "${SRC_VOLUME}:/src:ro" \
      -v "$(host_path "$PWD"):/out" \
      alpine tar czf "/out/$ARCHIVE" -C /src .

    echo "Done: $(du -h "$ARCHIVE" | cut -f1)"
    echo "This archive contains decryptable credentials. Do not commit it."
    ;;

  push)
    HOST="${2:?usage: push <user@host> <keyfile>}"
    KEY="${3:?usage: push <user@host> <keyfile>}"
    SSH_OPTS=(-i "$KEY" -o StrictHostKeyChecking=accept-new -o ServerAliveInterval=15 -o ServerAliveCountMax=4)

    LOCAL_SIZE=$(stat -c %s "$ARCHIVE")
    # scp restarts from zero on a dropped link, so append only the missing tail.
    for attempt in $(seq 1 25); do
      REMOTE_SIZE=$(ssh "${SSH_OPTS[@]}" "$HOST" "stat -c %s ~/$ARCHIVE 2>/dev/null || echo 0")
      [ "$REMOTE_SIZE" -ge "$LOCAL_SIZE" ] && break
      echo "Attempt $attempt: resuming at $REMOTE_SIZE / $LOCAL_SIZE bytes"
      # A partial from a dead scp is a valid prefix, so appending is safe.
      tail -c "+$((REMOTE_SIZE + 1))" "$ARCHIVE" \
        | ssh "${SSH_OPTS[@]}" "$HOST" "cat >> ~/$ARCHIVE" || true
    done

    echo "Verifying the transfer end to end..."
    LOCAL_SUM=$(sha256sum "$ARCHIVE" | cut -d' ' -f1)
    REMOTE_SUM=$(ssh "${SSH_OPTS[@]}" "$HOST" "sha256sum ~/$ARCHIVE | cut -d' ' -f1")
    if [ "$LOCAL_SUM" != "$REMOTE_SUM" ]; then
      echo "CHECKSUM MISMATCH -- delete ~/$ARCHIVE on the host and re-run push." >&2
      exit 1
    fi
    echo "Checksums match: $LOCAL_SUM"

    ssh "${SSH_OPTS[@]}" "$HOST" "mkdir -p ~/jenkins"
    scp "${SSH_OPTS[@]}" "$HERE/controller/compose.yaml" "$HERE/controller/Caddyfile" "$HOST:~/jenkins/"
    echo "Copied. Now ssh over and run: ./migrate-jenkins-home.sh import"
    ;;

  import)
    # Must run before the first 'compose up': Jenkins initialises an empty home
    # on boot, and a populated volume is what stops it showing the setup wizard.
    echo "Creating the volume and restoring into it..."
    docker volume create "$SRC_VOLUME" >/dev/null

    docker run --rm \
      -v "${SRC_VOLUME}:/dst" \
      -v "$HOME:/in:ro" \
      alpine sh -c "tar xzf /in/$ARCHIVE -C /dst"

    # The image runs as uid 1000; a restore as root leaves everything unwritable.
    docker run --rm -v "${SRC_VOLUME}:/dst" alpine chown -R 1000:1000 /dst

    echo "Restored. Verifying the credential store came across:"
    # hudson.util.Secret is the actual encryption key; master.key alone decrypts nothing.
    docker run --rm -v "${SRC_VOLUME}:/dst:ro" alpine sh -c '
      for f in secrets/master.key secrets/hudson.util.Secret credentials.xml; do
        [ -s "/dst/$f" ] && echo "  ok      $f" || echo "  MISSING $f"
      done
      echo "Credentials found:"
      grep -o "<id>[^<]*</id>" /dst/credentials.xml 2>/dev/null | sed "s/<[^>]*>//g;s/^/  /"
    ' || true

    echo
    echo "Now: write ~/jenkins/.env, then 'cd ~/jenkins && docker compose up -d'."
    echo "Delete ~/$ARCHIVE once Jenkins is confirmed working."
    ;;

  *) usage ;;
esac
