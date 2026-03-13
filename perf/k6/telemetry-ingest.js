import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: Number(__ENV.VUS || 20),
  duration: __ENV.DURATION || '2m',
  thresholds: {
    http_req_failed: ['rate<0.02'],
    http_req_duration: ['p(95)<750']
  }
};

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const payload = JSON.stringify({
    patientId: 'p-001',
    timestamp: new Date().toISOString(),
    gaitVariance: 0.41,
    confusionScore: 0.63,
    ambientLux: 220,
    roomTemperature: 25.2,
    source: 'k6'
  });

  const res = http.post(`${baseUrl}/api/v1/telemetry/ingest`, payload, {
    headers: {
      'Content-Type': 'application/json'
    }
  });

  check(res, {
    'telemetry ingest accepted': (r) => r.status === 200 || r.status === 202
  });

  sleep(0.3);
}
