package com.memoria.orchestrator.service;

import com.memoria.orchestrator.integration.BhashiniClient;
import com.memoria.orchestrator.integration.GeminiClient;
import com.memoria.orchestrator.integration.TwilioClient;
import com.memoria.orchestrator.model.DiagnosisRequest;
import com.memoria.orchestrator.model.DiagnosisResponse;
import com.memoria.orchestrator.model.InterventionRequest;
import com.memoria.orchestrator.model.InterventionResponse;
import com.memoria.orchestrator.model.TherapyPayload;
import org.springframework.stereotype.Service;

@Service
public class InterventionService {

    private final DiagnosisService diagnosisService;
    private final TherapyService therapyService;
    private final GeminiClient geminiClient;
    private final GeminiDiagnosisParserService parserService;
    private final AiOutputSafetyService safetyService;
    private final TwilioClient twilioClient;
    private final BhashiniClient bhashiniClient;

    public InterventionService(
            DiagnosisService diagnosisService,
            TherapyService therapyService,
            GeminiClient geminiClient,
            GeminiDiagnosisParserService parserService,
            AiOutputSafetyService safetyService,
            TwilioClient twilioClient,
            BhashiniClient bhashiniClient
    ) {
        this.diagnosisService = diagnosisService;
        this.therapyService = therapyService;
        this.geminiClient = geminiClient;
        this.parserService = parserService;
        this.safetyService = safetyService;
        this.twilioClient = twilioClient;
        this.bhashiniClient = bhashiniClient;
    }

    public InterventionResponse process(InterventionRequest request) {
        String rawModelOutput = geminiClient.summarizeNarrative(request.narrative()).join();

        DiagnosisResponse parsed = parserService.parse(rawModelOutput)
                .orElseGet(() -> diagnosisService.diagnose(new DiagnosisRequest(request.patientId(), request.narrative())));
        DiagnosisResponse diagnosis = safetyService.sanitizeDiagnosis(parsed);

        TherapyPayload therapy = safetyService.sanitizeTherapy(therapyService.generate(request.patientId(), "anxious"));

        boolean notified = false;
        if (diagnosis.caregiverNotify()) {
            notified = twilioClient.sendSms(request.caregiverPhone(),
                    "Memoria alert: " + diagnosis.condition() + " detected for patient " + request.patientId())
                    .join()
                    .success();
        }

        String language = request.preferredLanguage() == null ? "en" : request.preferredLanguage();
        boolean audioPrepared = bhashiniClient.synthesize(therapy.instruction(), language).join().success();

        return new InterventionResponse(diagnosis, therapy.mode(), notified, audioPrepared);
    }
}
