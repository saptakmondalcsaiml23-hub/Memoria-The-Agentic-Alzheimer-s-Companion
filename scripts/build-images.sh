#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

OWNER="${OWNER:-local}"
TAG="${TAG:-dev}"

build_image() {
  local service="$1"
  local dockerfile="$2"
  local image="ghcr.io/${OWNER}/memoria-${service}:${TAG}"

  echo "[images] building ${image}"
  docker build -f "${dockerfile}" -t "${image}" .
}

build_image "gateway-service" "gateway-service/Dockerfile"
build_image "telemetry-service" "telemetry-service/Dockerfile"
build_image "orchestrator-service" "orchestrator-service/Dockerfile"
build_image "identity-service" "identity-service/Dockerfile"

echo "[images] complete"
