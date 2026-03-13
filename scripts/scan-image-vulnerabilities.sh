#!/usr/bin/env bash
set -euo pipefail

OWNER="${OWNER:?OWNER is required}"
SERVICE="${SERVICE:?SERVICE is required}"
DIGEST="${DIGEST:?DIGEST is required}"
SEVERITY="${SEVERITY:-HIGH,CRITICAL}"

if [[ ! "$DIGEST" =~ ^sha256:[a-f0-9]{64}$ ]]; then
  echo "DIGEST must be in the form sha256:<64-hex>"
  exit 1
fi

if ! command -v trivy >/dev/null 2>&1; then
  echo "trivy is not installed. Install from https://trivy.dev/latest/getting-started/installation/"
  exit 1
fi

IMAGE="ghcr.io/${OWNER}/memoria-${SERVICE}@${DIGEST}"

echo "[scan] image: ${IMAGE}"
trivy image \
  --severity "${SEVERITY}" \
  --ignore-unfixed \
  --exit-code 1 \
  --format table \
  "${IMAGE}"

echo "[scan] no blocking vulnerabilities found for severity ${SEVERITY}"
