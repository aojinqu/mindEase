package com.mindease.domain.service;

import java.util.Map;

public class AiAnalysisService {
    public String generateSummaryWithFallback(
            int days,
            int total,
            int positive,
            int neutral,
            int negative,
            Map<String, Integer> tagFrequency
    ) {
        try {
            return generateByRemote(days, total, positive, neutral, negative, tagFrequency);
        } catch (Exception ignored) {
            return null;
        }
    }

    protected String generateByRemote(
            int days,
            int total,
            int positive,
            int neutral,
            int negative,
            Map<String, Integer> tagFrequency
    ) {
        throw new IllegalStateException("Remote AI service is unavailable.");
    }
}
