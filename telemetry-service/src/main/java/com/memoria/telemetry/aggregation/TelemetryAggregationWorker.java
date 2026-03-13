package com.memoria.telemetry.aggregation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelemetryAggregationWorker {

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String, StringBuilder> perPatientWindow = new ConcurrentHashMap<>();

    public TelemetryAggregationWorker(ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @KafkaListener(topics = "${memoria.kafka.sensor-topic}")
    public void onTelemetry(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String patientId = root.path("patientId").asText("unknown");
            Instant ts = Instant.parse(root.path("timestamp").asText(Instant.now().toString()));
            JsonNode events = root.path("events");

            StringBuilder builder = perPatientWindow.computeIfAbsent(patientId, k -> new StringBuilder());
            builder.append("[").append(ts).append("] ");
            for (JsonNode event : events) {
                builder.append(event.path("metricType").asText())
                        .append(":")
                        .append(event.path("value").asText())
                        .append("; ");
            }
            builder.append("\n");
        } catch (Exception ignored) {
            // Invalid payloads are ignored in this starter worker.
        }
    }

    @Scheduled(fixedRate = 300000)
    public void flushFiveMinuteWindows() {
        for (Map.Entry<String, StringBuilder> entry : perPatientWindow.entrySet()) {
            String patientId = entry.getKey();
            String summary = entry.getValue().toString();
            if (summary.isBlank()) {
                continue;
            }

            jdbcTemplate.update(
                    "INSERT INTO telemetry_aggregates(patient_id, window_end, summary_text) VALUES (?, ?, ?)",
                    patientId,
                    Timestamp.from(Instant.now()),
                    summary
            );

            LocalDate date = Instant.now().atZone(ZoneOffset.UTC).toLocalDate();
            String vec = toVector(summary);
            jdbcTemplate.update(
                    "INSERT INTO daily_evaluations(patient_id, eval_date, summary_text, embedding_vector) VALUES (?, ?, ?, CAST(? AS vector)) " +
                            "ON CONFLICT (patient_id, eval_date) DO UPDATE SET summary_text = EXCLUDED.summary_text, embedding_vector = EXCLUDED.embedding_vector",
                    patientId,
                    date,
                    summary,
                    vec
            );

            entry.getValue().setLength(0);
        }
    }

    private String toVector(String text) {
        String hash = DigestUtils.sha256Hex(text);
        int a = Integer.parseInt(hash.substring(0, 4), 16);
        int b = Integer.parseInt(hash.substring(4, 8), 16);
        int c = Integer.parseInt(hash.substring(8, 12), 16);
        int d = Integer.parseInt(hash.substring(12, 16), 16);
        return "[" + normalize(a) + "," + normalize(b) + "," + normalize(c) + "," + normalize(d) + "]";
    }

    private double normalize(int value) {
        return value / 65535.0;
    }
}
