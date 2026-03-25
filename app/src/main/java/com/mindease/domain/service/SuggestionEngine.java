package com.mindease.domain.service;

public class SuggestionEngine {
    public String generateSimpleSuggestion(int negativeCount) {
        if (negativeCount >= 3) {
            return "Try a 2-minute breathing break and reduce information input tonight.";
        }
        return "Keep your routine stable and continue daily mood check-ins.";
    }
}
