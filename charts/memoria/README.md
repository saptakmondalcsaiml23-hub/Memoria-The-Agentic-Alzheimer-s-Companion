# Memoria Helm Chart

Install the Memoria backend stack resources:

```bash
helm upgrade --install memoria ./charts/memoria \
  --namespace memoria \
  --create-namespace
```

Override defaults for each environment with a values file:

```bash
helm upgrade --install memoria ./charts/memoria \
  --namespace memoria \
  --create-namespace \
  -f ./charts/memoria/values-prod.yaml
```

The chart defines deployments and services for:
- `gateway-service`
- `telemetry-service` (with optional HPA)
- `orchestrator-service`
- `identity-service`

Optional gateway ingress can be enabled through values:

```yaml
ingress:
  enabled: true
  className: nginx
  host: memoria.example.com
  tls:
    enabled: true
    secretName: memoria-gateway-tls
```
