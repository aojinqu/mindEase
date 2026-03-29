package com.mindease.domain.model;

import java.util.Collections;
import java.util.Map;

public class AnalysisReport {
    public final int totalCount;
    public final int positiveCount;
    public final int neutralCount;
    public final int negativeCount;
    public final Map<String, Integer> tagFrequency;
    public final String summaryText;

    public AnalysisReport(
            int totalCount,
            int positiveCount,
            int neutralCount,
            int negativeCount,
            Map<String, Integer> tagFrequency,
            String summaryText
    ) {
        this.totalCount = totalCount;
        this.positiveCount = positiveCount;
        this.neutralCount = neutralCount;
        this.negativeCount = negativeCount;
        this.tagFrequency = Collections.unmodifiableMap(tagFrequency);
        this.summaryText = summaryText;
    }
}
