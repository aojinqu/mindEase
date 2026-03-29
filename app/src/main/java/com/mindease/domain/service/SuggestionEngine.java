package com.mindease.domain.service;

import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.Suggestion;

import java.util.UUID;

public class SuggestionEngine {
    public Suggestion generateFromReport(AnalysisReport report) {
        if (report.negativeCount >= 3) {
            return new Suggestion(
                    UUID.randomUUID().toString(),
                    "Try a 2-minute breathing break and reduce information input tonight.",
                    "stress_relief"
            );
        }
        if (report.positiveCount >= report.negativeCount && report.totalCount > 0) {
            return new Suggestion(
                    UUID.randomUUID().toString(),
                    "Your mood trend is relatively stable. Keep your current sleep and study rhythm.",
                    "stability"
            );
        }
        return new Suggestion(
                UUID.randomUUID().toString(),
                "Take a short mindful walk and write one sentence about your current feeling.",
                "self_checkin"
        );
    }
}
