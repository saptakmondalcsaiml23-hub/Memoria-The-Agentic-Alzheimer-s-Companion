package com.memoria.orchestrator.service;

import com.memoria.orchestrator.model.DiagnosisResponse;
import com.memoria.orchestrator.model.TherapyPayload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class AiOutputSafetyService {

    private static final Set<String> ALLOWED_ACTIONS = Set.of(
            "none",
            "launch_grounding_trivia",
            "trigger_music_therapy",
            "notify_caregiver",
            "offline_fallback"
    );

    private static final Pattern MEDICAL_PATTERN = Pattern.compile(
            "(?i)(medication|tablet|dose|pill|drug|prescribe|injection|mg\\b)"
    );

    public DiagnosisResponse sanitizeDiagnosis(DiagnosisResponse diagnosis) {
        if (diagnosis == null) {
            return fallbackDiagnosis();
        }

        if (!ALLOWED_ACTIONS.contains(diagnosis.action())) {
            return fallbackDiagnosis();
        }

        if (containsMedicalAdvice(diagnosis.condition()) || containsMedicalAdvice(diagnosis.action())) {
            return fallbackDiagnosis();
        }

        int boundedScore = Math.max(0, Math.min(10, diagnosis.anomalyScore()));
        return new DiagnosisResponse(
                boundedScore,
                diagnosis.condition(),
                diagnosis.action(),
                diagnosis.urgency(),
                diagnosis.caregiverNotify()
        );
    }

    public TherapyPayload sanitizeTherapy(TherapyPayload payload) {
        if (payload == null) {
            return safeFallbackTherapy();
        }

        String instruction = containsMedicalAdvice(payload.instruction())
                ? "Let's relax with calming music and a simple memory game."
                : payload.instruction();

        List<String> prompts = payload.prompts() == null
                ? List.of("Tell me about a favorite place from your childhood")
                : payload.prompts().stream()
                .map(p -> containsMedicalAdvice(p) ? "Share a comforting family memory" : p)
                .toList();

        return new TherapyPayload(
                payload.patientId(),
                payload.mode(),
                payload.title(),
                instruction,
                prompts,
                payload.musicHint()
        );
    }

    private boolean containsMedicalAdvice(String text) {
        return text != null && MEDICAL_PATTERN.matcher(text).find();
    }

    private DiagnosisResponse fallbackDiagnosis() {
        return new DiagnosisResponse(2, "requires_review", "offline_fallback", "low", false);
    }

    private TherapyPayload safeFallbackTherapy() {
        return new TherapyPayload(
                "unknown",
                "light_trivia",
                "Calm Mode",
                "Let's take a calm breath and recall a happy memory.",
                List.of("What is your favorite song from your youth?"),
                "Soft instrumental"
        );
    }
}
