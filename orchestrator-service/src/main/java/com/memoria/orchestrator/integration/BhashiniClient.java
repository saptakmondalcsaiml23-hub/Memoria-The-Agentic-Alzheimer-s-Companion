package com.memoria.orchestrator.integration;

import com.memoria.orchestrator.integration.dto.ProviderOutcome;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class BhashiniClient {

    private final WebClient webClient;
    private final String apiKey;

    public BhashiniClient(
            WebClient.Builder builder,
            @Value("${memoria.bhashini.base-url}") String baseUrl,
            @Value("${memoria.bhashini.api-key}") String apiKey
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @CircuitBreaker(name = "bhashiniClient", fallbackMethod = "fallback")
    @TimeLimiter(name = "bhashiniClient")
    public CompletableFuture<ProviderOutcome> synthesize(String text, String languageCode) {
        return webClient.post()
                .uri("/services/inference/pipeline")
                .header("Authorization", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("text", text, "lang", languageCode))
                .retrieve()
                .bodyToMono(String.class)
                .map(r -> new ProviderOutcome(true, "bhashini", "audio_ready"))
                .toFuture();
    }

    private CompletableFuture<ProviderOutcome> fallback(String text, String languageCode, Throwable throwable) {
        return CompletableFuture.completedFuture(new ProviderOutcome(false, "bhashini", "fallback"));
    }
}
