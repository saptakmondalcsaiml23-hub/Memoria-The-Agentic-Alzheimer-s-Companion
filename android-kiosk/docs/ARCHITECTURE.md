# Android Kiosk Module (Starter)

This repository currently provides backend and infrastructure scaffolding. The Android kiosk app is represented as a design placeholder in this iteration.

## Planned Android capabilities

- Kiosk mode launcher replacement
- SensorManager capture (accelerometer + interaction timing)
- Edge heuristic monitor for anomaly threshold detection
- Local offline therapy fallback (SQLite + media pack)
- Secure device identity provisioning with certificate pinning

## Suggested package structure

- `app/src/main/java/com/memoria/kiosk/monitor`
- `app/src/main/java/com/memoria/kiosk/therapy`
- `app/src/main/java/com/memoria/kiosk/network`
- `app/src/main/java/com/memoria/kiosk/security`

## Integration contract

The kiosk should push batched telemetry payloads to:

- `POST /api/v1/telemetry/ingest`

And receive therapy payloads from:

- `GET /api/v1/therapy/generate`
