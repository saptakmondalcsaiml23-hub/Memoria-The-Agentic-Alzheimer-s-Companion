# mTLS and Zero-Trust Baseline

This folder contains baseline production templates for service-to-service hardening.

## Included Templates

- `networkpolicy-default-deny.yaml`: deny all ingress/egress by default.
- `networkpolicy-allow-gateway.yaml`: allow gateway traffic to internal services.
- `istio-peer-authentication-strict.yaml`: enforce strict Istio mTLS.
- `istio-destination-rule-mtls.yaml`: configure ISTIO_MUTUAL for internal traffic.

## Apply Order

1. Apply namespace and core workloads.
2. Apply default deny policy.
3. Apply explicit allow policies.
4. Apply Istio strict mTLS resources.

## Notes

- Validate DNS host patterns before applying destination rules in multi-namespace topologies.
- For non-Istio clusters, use cert-manager and sidecars/proxies with client cert verification.
- Keep private keys in external secret stores (GCP Secret Manager, Vault).
