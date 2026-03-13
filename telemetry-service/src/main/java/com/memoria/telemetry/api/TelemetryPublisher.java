package com.memoria.telemetry.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoria.telemetry.model.TelemetryIngestRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TelemetryPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public TelemetryPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${memoria.kafka.sensor-topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public void publish(TelemetryIngestRequest request, String correlationId) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(topic, request.patientId(), payload)
                    .thenAccept(result -> {
                        // The callback is intentionally no-op in the starter template.
                    });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize telemetry payload for correlationId=" + correlationId, e);
        }
    }
}
