import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: Number(__ENV.VUS || 10),
  duration: __ENV.DURATION || '2m',
  thresholds: {
    http_req_failed: ['rate<0.02'],
    http_req_duration: ['p(95)<1500']
  }
};

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const payload = JSON.stringify({
    patientId: 'p-001',
    contextWindowMinutes: 10,
    symptoms: ['wandering risk', 'elevated confusion'],
    telemetrySnapshot: {
      gaitVariance: 0.48,
      confusionScore: 0.71,
      ambientLux: 180
    }
  });

  const res = http.post(`${baseUrl}/api/v1/orchestrator/analyze`, payload, {
    headers: {
      'Content-Type': 'application/json'
    }
  });

  check(res, {
    'analyze returns 200': (r) => r.status === 200
  });

  sleep(0.5);
}
