#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
DURATION="${DURATION:-2m}"
VUS_TELEMETRY="${VUS_TELEMETRY:-20}"
VUS_ANALYZE="${VUS_ANALYZE:-10}"

if ! command -v k6 >/dev/null 2>&1; then
  echo "k6 is not installed. Install from https://k6.io/docs/get-started/installation/"
  exit 1
fi

echo "Running telemetry ingest load test against ${BASE_URL}"
k6 run \
  -e BASE_URL="${BASE_URL}" \
  -e DURATION="${DURATION}" \
  -e VUS="${VUS_TELEMETRY}" \
  perf/k6/telemetry-ingest.js

echo "Running orchestrator analyze load test against ${BASE_URL}"
k6 run \
  -e BASE_URL="${BASE_URL}" \
  -e DURATION="${DURATION}" \
  -e VUS="${VUS_ANALYZE}" \
  perf/k6/orchestrator-analyze.js
