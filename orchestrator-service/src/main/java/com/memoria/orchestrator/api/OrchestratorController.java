package com.memoria.orchestrator.api;

import com.memoria.orchestrator.model.ClinicalVisionEvalResponse;
import com.memoria.orchestrator.model.DiagnosisRequest;
import com.memoria.orchestrator.model.DiagnosisResponse;
import com.memoria.orchestrator.model.InterventionRequest;
import com.memoria.orchestrator.model.InterventionResponse;
import com.memoria.orchestrator.model.TherapyPayload;
import com.memoria.orchestrator.service.DiagnosisService;
import com.memoria.orchestrator.service.InterventionService;
import com.memoria.orchestrator.service.TherapyService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class OrchestratorController {

    private final DiagnosisService diagnosisService;
    private final TherapyService therapyService;
    private final InterventionService interventionService;

    public OrchestratorController(
            DiagnosisService diagnosisService,
            TherapyService therapyService,
            InterventionService interventionService
    ) {
        this.diagnosisService = diagnosisService;
        this.therapyService = therapyService;
        this.interventionService = interventionService;
    }

    @PostMapping("/orchestrator/analyze")
    public DiagnosisResponse analyze(@Valid @RequestBody DiagnosisRequest request) {
        return diagnosisService.diagnose(request);
    }

    @PostMapping("/orchestrator/intervene")
    public InterventionResponse intervene(@Valid @RequestBody InterventionRequest request) {
        return interventionService.process(request);
    }

    @GetMapping("/therapy/generate")
    public TherapyPayload generateTherapy(
            @RequestParam String patientId,
            @RequestParam(required = false, defaultValue = "neutral") String mood
    ) {
        return therapyService.generate(patientId, mood);
    }

    @PostMapping(value = "/clinical/vision-eval", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClinicalVisionEvalResponse evaluateClockDraw(
            @RequestParam String patientId,
            @RequestPart MultipartFile file
    ) {
        int score = file.isEmpty() ? 0 : 7;
        String assessment = score >= 7 ? "mild_impairment_or_better" : "requires_review";
        return new ClinicalVisionEvalResponse(
                patientId,
                assessment,
                score,
                "Starter scorer; replace with Gemini Vision pipeline"
        );
    }
}
