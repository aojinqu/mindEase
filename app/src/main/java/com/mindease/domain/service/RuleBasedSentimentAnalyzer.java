package com.mindease.domain.service;

public class RuleBasedSentimentAnalyzer {
    public float analyzeScore(String text, int intensity) {
        if (text == null || text.trim().isEmpty()) {
            return 0f;
        }
        return Math.max(-1f, Math.min(1f, (intensity - 3) / 2.0f));
    }
}
