# k6 Performance Tests

This folder contains baseline load tests for critical API paths:
- `telemetry-ingest.js`
- `orchestrator-analyze.js`

## Local usage

Install k6 and run:

```bash
BASE_URL=http://localhost:8080 DURATION=2m VUS_TELEMETRY=20 VUS_ANALYZE=10 ./scripts/run-k6.sh
```

## CI usage

Use the `Load Test` workflow and provide `base_url` for the deployed gateway endpoint.
