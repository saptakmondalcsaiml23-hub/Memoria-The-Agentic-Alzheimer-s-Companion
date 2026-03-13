package com.memoria.telemetry.api;

import com.memoria.telemetry.model.TelemetryIngestRequest;
import com.memoria.telemetry.model.TelemetryIngestResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final TelemetryPublisher telemetryPublisher;

    public TelemetryController(TelemetryPublisher telemetryPublisher) {
        this.telemetryPublisher = telemetryPublisher;
    }

    @PostMapping("/ingest")
    public ResponseEntity<TelemetryIngestResponse> ingest(@Valid @RequestBody TelemetryIngestRequest request) {
        String correlationId = UUID.randomUUID().toString();
        telemetryPublisher.publish(request, correlationId);

        return ResponseEntity.accepted().body(new TelemetryIngestResponse(
                "accepted",
                correlationId,
                Instant.now()
        ));
    }
}
