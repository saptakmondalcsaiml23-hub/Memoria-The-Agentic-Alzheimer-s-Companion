package com.memoria.orchestrator.service;

import com.memoria.orchestrator.model.DiagnosisRequest;
import com.memoria.orchestrator.model.DiagnosisResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosisService {

    private final RagRetrievalService ragRetrievalService;
    private final VectorizationService vectorizationService;

    public DiagnosisService(RagRetrievalService ragRetrievalService, VectorizationService vectorizationService) {
        this.ragRetrievalService = ragRetrievalService;
        this.vectorizationService = vectorizationService;
    }

    public DiagnosisResponse diagnose(DiagnosisRequest request) {
        String narrative = request.telemetryNarrative().toLowerCase();
        String vector = vectorizationService.toVectorLiteral(request.telemetryNarrative());
        List<String> similarDays = ragRetrievalService.topSimilarDays(request.patientId(), vector, 3);
        boolean historicallyRecurring = !similarDays.isEmpty();

        if (narrative.contains("tremor") || narrative.contains("rapid ui") || narrative.contains("panic")) {
            return new DiagnosisResponse(historicallyRecurring ? 6 : 8, "sundowning_anxiety", "trigger_music_therapy", "medium", true);
        }

        if (narrative.contains("dialer") || narrative.contains("erratic app switching")) {
            return new DiagnosisResponse(historicallyRecurring ? 5 : 7, "cognitive_looping", "launch_grounding_trivia", "medium", true);
        }

        return new DiagnosisResponse(2, "stable", "none", "low", false);
    }
}
