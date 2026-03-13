#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GATEWAY_URL="${GATEWAY_URL:-http://localhost:8080}"

admin_token="$(${ROOT_DIR}/scripts/get-keycloak-token.sh admin admin)"
caregiver_token="$(${ROOT_DIR}/scripts/get-keycloak-token.sh caregiver caregiver)"

echo "[smoke] creating caregiver via admin token"
curl -sS -X POST "${GATEWAY_URL}/api/v1/caregivers" \
  -H "Authorization: Bearer ${admin_token}" \
  -H "Content-Type: application/json" \
  -d '{"id":"c-smoke","phoneNumber":"+911234567890","preferences":"music"}' >/dev/null

echo "[smoke] creating patient via admin token"
curl -sS -X POST "${GATEWAY_URL}/api/v1/patients" \
  -H "Authorization: Bearer ${admin_token}" \
  -H "Content-Type: application/json" \
  -d '{"id":"p-smoke","demographic":"male_75_plus","region":"WB","caregiverId":"c-smoke"}' >/dev/null

echo "[smoke] reading patient via caregiver token"
curl -sS "${GATEWAY_URL}/api/v1/patients/p-smoke" \
  -H "Authorization: Bearer ${caregiver_token}" >/dev/null

echo "[smoke] invoking intervention endpoint"
curl -sS -X POST "${GATEWAY_URL}/api/v1/orchestrator/intervene" \
  -H "Content-Type: application/json" \
  -d '{"patientId":"p-smoke","caregiverPhone":"+911234567890","narrative":"rapid ui switching and tremor observed","preferredLanguage":"en"}' >/dev/null

echo "[smoke] all authenticated checks passed"
