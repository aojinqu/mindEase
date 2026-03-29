package com.mindease.domain.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RuleBasedSentimentAnalyzer {
    private static final Set<String> POSITIVE_KEYWORDS = new HashSet<>(Arrays.asList(
            "happy", "calm", "good", "relaxed", "grateful", "great", "joy", "hopeful"
    ));

    private static final Set<String> NEGATIVE_KEYWORDS = new HashSet<>(Arrays.asList(
            "sad", "anxious", "angry", "stress", "stressed", "tired", "lonely", "bad"
    ));

    public float analyzeScore(String text, int intensity) {
        float intensityScore = (intensity - 3) / 2.0f;
        float keywordScore = evaluateKeywordScore(text);
        float raw = (intensityScore * 0.6f) + (keywordScore * 0.4f);
        if (raw > 1f) {
            return 1f;
        }
        if (raw < -1f) {
            return -1f;
        }
        return raw;
    }

    public String analyzeLabel(String text, int intensity) {
        float score = analyzeScore(text, intensity);
        if (score > 0.25f) {
            return "positive";
        }
        if (score < -0.25f) {
            return "negative";
        }
        return "neutral";
    }

    private float evaluateKeywordScore(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0f;
        }
        String lower = text.toLowerCase(Locale.US);
        int positive = 0;
        int negative = 0;
        for (String keyword : POSITIVE_KEYWORDS) {
            if (lower.contains(keyword)) {
                positive++;
            }
        }
        for (String keyword : NEGATIVE_KEYWORDS) {
            if (lower.contains(keyword)) {
                negative++;
            }
        }
        int total = positive + negative;
        if (total == 0) {
            return 0f;
        }
        return (float) (positive - negative) / total;
    }
}
