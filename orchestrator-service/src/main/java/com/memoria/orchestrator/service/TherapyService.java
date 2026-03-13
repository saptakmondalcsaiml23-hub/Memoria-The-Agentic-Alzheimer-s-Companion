package com.memoria.orchestrator.service;

import com.memoria.orchestrator.model.TherapyPayload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TherapyService {

    public TherapyPayload generate(String patientId, String mood) {
        String normalizedMood = mood == null ? "neutral" : mood.toLowerCase();

        if ("anxious".equals(normalizedMood)) {
            return new TherapyPayload(
                    patientId,
                    "music_and_trivia",
                    "Calm Evening Routine",
                    "Let's take a deep breath and play a short memory game.",
                    List.of("Name a favorite movie from the 1970s", "Which city did you grow up in?"),
                    "Play warm regional classics from the 1970s"
            );
        }

        return new TherapyPayload(
                patientId,
                "light_trivia",
                "Daily Memory Spark",
                "Try these simple, familiar prompts.",
                List.of("What was your first school called?", "Who was your childhood best friend?"),
                "Optional soft instrumental"
        );
    }
}
