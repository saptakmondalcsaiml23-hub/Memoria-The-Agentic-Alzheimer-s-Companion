package com.memoria.orchestrator.integration;

import com.memoria.orchestrator.integration.dto.ProviderOutcome;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Component
public class TwilioClient {

    private final WebClient webClient;
    private final String accountSid;
    private final String authToken;
    private final String fromNumber;

    public TwilioClient(
            WebClient.Builder builder,
            @Value("${memoria.twilio.base-url}") String baseUrl,
            @Value("${memoria.twilio.account-sid}") String accountSid,
            @Value("${memoria.twilio.auth-token}") String authToken,
            @Value("${memoria.twilio.from-number}") String fromNumber
    ) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
    }

    @CircuitBreaker(name = "twilioClient", fallbackMethod = "fallback")
    @TimeLimiter(name = "twilioClient")
    public CompletableFuture<ProviderOutcome> sendSms(String to, String body) {
        return webClient.post()
                .uri("/2010-04-01/Accounts/{sid}/Messages.json", accountSid)
                .headers(h -> h.setBasicAuth(accountSid, authToken))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("From", fromNumber)
                        .with("To", to)
                        .with("Body", body))
                .retrieve()
                .bodyToMono(String.class)
                .map(r -> new ProviderOutcome(true, "twilio", "queued"))
                .toFuture();
    }

    private CompletableFuture<ProviderOutcome> fallback(String to, String body, Throwable throwable) {
        return CompletableFuture.completedFuture(new ProviderOutcome(false, "twilio", "fallback"));
    }
}
