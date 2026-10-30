#!/usr/bin/env bash
# Build and start the tea register in Docker: Postgres, API, and UI.
# Usage: ./scripts/deploy.sh
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SECRET_DIR="$ROOT/.docker-secrets"
mkdir -p "$SECRET_DIR"
umask 077

prop() {
  local file="$1" name="$2" line
  line="$(grep -E "^${name}=" "$file" | head -n 1 || true)"
  printf '%s' "${line#*=}"
}

ACTOR=""
TOKEN=""
if [[ -f "$HOME/.gradle/gradle.properties" ]]; then
  ACTOR="$(prop "$HOME/.gradle/gradle.properties" gpr.user)"
  TOKEN="$(prop "$HOME/.gradle/gradle.properties" gpr.key)"
fi
ACTOR="${ACTOR:-${GITHUB_ACTOR:-}}"
TOKEN="${TOKEN:-${GITHUB_TOKEN:-}}"

if [[ -z "$ACTOR" || -z "$TOKEN" ]]; then
  cat <<'EOF' >&2
The images need a GitHub classic token with read:packages to download the Helex libraries.

Create one:
  https://github.com/settings/tokens/new?description=ITE4120+Helex+packages+read-only&scopes=read:packages

Then either save it the way the README describes
  (~/.gradle/gradle.properties and npm config),
or set GITHUB_ACTOR and GITHUB_TOKEN and run this script again.
EOF
  exit 1
fi

printf 'gpr.user=%s\ngpr.key=%s\n' "$ACTOR" "$TOKEN" > "$SECRET_DIR/gradle.properties"
if [[ -f "$HOME/.npmrc" ]] && grep -q 'npm\.pkg\.github\.com/:_authToken=\S' "$HOME/.npmrc"; then
  cp "$HOME/.npmrc" "$SECRET_DIR/npmrc"
else
  printf '//npm.pkg.github.com/:_authToken=%s\n' "$TOKEN" > "$SECRET_DIR/npmrc"
fi

docker compose -f "$ROOT/docker/compose.app.yaml" up -d --build

cat <<'EOF'

Application   http://localhost:18640
Swagger UI    http://localhost:18440/swagger-ui.html
PostgreSQL    localhost:18520, database tea
EOF
