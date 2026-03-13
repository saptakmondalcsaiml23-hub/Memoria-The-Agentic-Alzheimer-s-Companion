package com.memoria.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.memoria.orchestrator.model.DiagnosisResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Set;
import java.util.Optional;

@Service
public class GeminiDiagnosisParserService {

    private final ObjectMapper objectMapper;
    private final JsonSchema schema;

    public GeminiDiagnosisParserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.schema = loadSchema(objectMapper);
    }

    public Optional<DiagnosisResponse> parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }

        String jsonSlice = extractJson(raw);
        if (jsonSlice == null) {
            return Optional.empty();
        }

        try {
            JsonNode node = objectMapper.readTree(jsonSlice);

            Set<ValidationMessage> errors = schema.validate(node);
            if (!errors.isEmpty()) {
                return Optional.empty();
            }

            int anomalyScore = node.path("anomaly_score").asInt(-1);
            String condition = node.path("condition").asText("");
            String action = node.path("action").asText("");
            String urgency = node.path("urgency").asText("low");
            boolean notify = node.path("caregiver_notify").asBoolean(false);

            if (anomalyScore < 0 || anomalyScore > 10 || condition.isBlank() || action.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(new DiagnosisResponse(anomalyScore, condition, action, urgency, notify));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        return raw.substring(start, end + 1);
    }

    private JsonSchema loadSchema(ObjectMapper mapper) {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            ClassPathResource resource = new ClassPathResource("schemas/diagnosis-output.schema.json");
            try (InputStream in = resource.getInputStream()) {
                JsonNode schemaNode = mapper.readTree(in);
                return factory.getSchema(schemaNode);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load diagnosis schema", e);
        }
    }
}
