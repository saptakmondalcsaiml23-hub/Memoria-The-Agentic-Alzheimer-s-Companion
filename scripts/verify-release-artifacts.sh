#!/usr/bin/env bash
set -euo pipefail

OWNER="${OWNER:?OWNER is required}"
SERVICE="${SERVICE:?SERVICE is required}"
DIGEST="${DIGEST:?DIGEST is required}"

if [[ ! "$DIGEST" =~ ^sha256:[a-f0-9]{64}$ ]]; then
  echo "DIGEST must be in the form sha256:<64-hex>"
  exit 1
fi

IMAGE="ghcr.io/${OWNER}/memoria-${SERVICE}@${DIGEST}"
WORKFLOW_IDENTITY_REGEX="https://github.com/.+/.+/.github/workflows/release-images.yml@.+"
OIDC_ISSUER="https://token.actions.githubusercontent.com"

echo "[verify] image: ${IMAGE}"

echo "[verify] checking image signature"
cosign verify \
  --certificate-oidc-issuer="${OIDC_ISSUER}" \
  --certificate-identity-regexp="${WORKFLOW_IDENTITY_REGEX}" \
  "${IMAGE}" >/dev/null

echo "[verify] checking build provenance attestation"
gh attestation verify "oci://${IMAGE}" --owner "${OWNER}" >/dev/null

echo "[verify] checking SPDX SBOM attestation"
cosign verify-attestation --type spdxjson \
  --certificate-oidc-issuer="${OIDC_ISSUER}" \
  --certificate-identity-regexp="${WORKFLOW_IDENTITY_REGEX}" \
  "${IMAGE}" >/dev/null

echo "[verify] all checks passed"
