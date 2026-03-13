package com.memoria.orchestrator.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RagRetrievalService {

    private final JdbcTemplate jdbcTemplate;

    public RagRetrievalService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> topSimilarDays(String patientId, String vectorLiteral, int k) {
        try {
            return jdbcTemplate.query(
                    "SELECT summary_text FROM daily_evaluations WHERE patient_id = ? ORDER BY embedding_vector <=> CAST(? AS vector) LIMIT ?",
                    (rs, rowNum) -> rs.getString(1),
                    patientId,
                    vectorLiteral,
                    k
            );
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }
}
