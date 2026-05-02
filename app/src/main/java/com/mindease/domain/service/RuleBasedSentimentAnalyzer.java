package com.mindease.domain.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RuleBasedSentimentAnalyzer {
    private static final Set<String> POSITIVE_MOODS = new HashSet<>(Arrays.asList(
        "happy", "joyful", "excited", "grateful", "relaxed", "hopeful", "good", "great",
        "cheerful", "content", "delighted", "peaceful", "smiling"
    ));

    private static final Set<String> NEUTRAL_MOODS = new HashSet<>(Arrays.asList(
        "calm", "neutral", "steady", "fine", "okay", "ok", "okayish", "balanced", "okayy"
    ));

    private static final Set<String> NEGATIVE_MOODS = new HashSet<>(Arrays.asList(
        "sad", "anxious", "anxiety", "angry", "stress", "stressed", "tired",
        "lonely", "bad", "upset", "down", "nervous", "mad", "unhappy",
        "worried", "frustrated", "overwhelmed", "depressed", "hurt"
    ));

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
        return analyzeLabel(null, text, intensity);
    }

    public String analyzeLabel(String moodType, String text, int intensity) {
        String moodCategory = classifyMoodType(moodType);
        if (moodCategory != null) {
            return moodCategory;
        }
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
        String normalized = normalizeText(text);
        int positive = 0;
        int negative = 0;
        for (String keyword : POSITIVE_KEYWORDS) {
            if (containsKeyword(normalized, keyword)) {
                positive++;
            }
        }
        for (String keyword : NEGATIVE_KEYWORDS) {
            if (containsKeyword(normalized, keyword)) {
                negative++;
            }
        }
        int total = positive + negative;
        if (total == 0) {
            return 0f;
        }
        return (float) (positive - negative) / total;
    }

    private String classifyMoodType(String moodType) {
        if (moodType == null || moodType.trim().isEmpty()) {
            return null;
        }
        String normalized = normalizeText(moodType);
        if (normalized.isEmpty()) {
            return null;
        }
        for (String keyword : NEGATIVE_MOODS) {
            if (containsKeyword(normalized, keyword)) {
                return "negative";
            }
        }
        for (String keyword : POSITIVE_MOODS) {
            if (containsKeyword(normalized, keyword)) {
                return "positive";
            }
        }
        for (String keyword : NEUTRAL_MOODS) {
            if (containsKeyword(normalized, keyword)) {
                return "neutral";
            }
        }
        return null;
    }

    private String normalizeText(String text) {
        return text.toLowerCase(Locale.US)
                .replaceAll("[^a-z]+", " ")
                .trim();
    }

    private boolean containsKeyword(String normalizedText, String keyword) {
        String normalizedKeyword = normalizeText(keyword);
        if (normalizedKeyword.isEmpty() || normalizedText.isEmpty()) {
            return false;
        }
        String paddedText = " " + normalizedText + " ";
        String paddedKeyword = " " + normalizedKeyword + " ";
        return paddedText.contains(paddedKeyword);
    }
}
