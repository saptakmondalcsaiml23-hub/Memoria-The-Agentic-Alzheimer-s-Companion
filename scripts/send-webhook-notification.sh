#!/usr/bin/env bash
set -euo pipefail

MESSAGE="${MESSAGE:?MESSAGE is required}"
DRY_RUN="${DRY_RUN:-0}"

if [[ "$DRY_RUN" != "1" ]]; then
  WEBHOOK_URL="${WEBHOOK_URL:?WEBHOOK_URL is required when DRY_RUN is not set}"
fi

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required but not installed"
  exit 1
fi

if command -v jq >/dev/null 2>&1; then
  payload="$(jq -n --arg text "$MESSAGE" '{text: $text}')"
else
  # Fallback minimal escaping when jq is unavailable.
  escaped_message="$(printf '%s' "$MESSAGE" | sed 's/\\/\\\\/g; s/"/\\"/g; :a;N;$!ba;s/\n/\\n/g')"
  payload="{\"text\":\"${escaped_message}\"}"
fi

if [[ "$DRY_RUN" == "1" ]]; then
  echo "$payload"
  exit 0
fi

curl -sS -X POST -H "Content-Type: application/json" -d "$payload" "$WEBHOOK_URL"
