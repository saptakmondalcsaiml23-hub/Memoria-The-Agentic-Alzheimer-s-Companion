#!/usr/bin/env bash
set -euo pipefail

KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8084}"
REALM="${KEYCLOAK_REALM:-memoria}"
CLIENT_ID="${KEYCLOAK_CLIENT_ID:-memoria-api}"
USERNAME="${1:-admin}"
PASSWORD="${2:-admin}"

resp="$(curl -sS -X POST "${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "grant_type=password" \
  --data-urlencode "client_id=${CLIENT_ID}" \
  --data-urlencode "username=${USERNAME}" \
  --data-urlencode "password=${PASSWORD}")"

token="$(printf "%s" "$resp" | sed -n 's/.*"access_token":"\([^"]*\)".*/\1/p')"

if [[ -z "$token" ]]; then
  echo "Failed to fetch token. Raw response:" >&2
  echo "$resp" >&2
  exit 1
fi

printf "%s\n" "$token"
