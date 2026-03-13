package com.memoria.orchestrator.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class GeminiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            WebClient.Builder builder,
            @Value("${memoria.ai.base-url}") String baseUrl,
            @Value("${memoria.ai.api-key}") String apiKey,
            @Value("${memoria.ai.model-flash}") String model
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    @CircuitBreaker(name = "geminiClient", fallbackMethod = "fallback")
    @TimeLimiter(name = "geminiClient")
    public CompletableFuture<String> summarizeNarrative(String narrative) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(model))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", narrative)})}))
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
    }

    private CompletableFuture<String> fallback(String narrative, Throwable throwable) {
        return CompletableFuture.completedFuture("{\"status\":\"fallback\",\"reason\":\"gemini_unavailable\"}");
    }
}
