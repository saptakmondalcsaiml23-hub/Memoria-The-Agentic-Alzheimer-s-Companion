#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

cleanup() {
  echo "[memoria] stopping local services"
  pkill -f "gateway-service" || true
  pkill -f "telemetry-service" || true
  pkill -f "orchestrator-service" || true
  pkill -f "identity-service" || true
}

trap cleanup EXIT

echo "[memoria] starting infra"
docker compose up -d zookeeper kafka redis postgres keycloak

echo "[memoria] starting services"
mvn -q -pl telemetry-service spring-boot:run &
mvn -q -pl orchestrator-service spring-boot:run &
mvn -q -pl identity-service spring-boot:run &
mvn -q -pl gateway-service spring-boot:run &

wait
