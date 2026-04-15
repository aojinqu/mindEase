package com.mindease.data.repository;

import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.repository.AnalysisRepository;
import com.mindease.domain.repository.MoodRepository;
import com.mindease.domain.service.AiAnalysisService;
import com.mindease.domain.service.RuleBasedSentimentAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisRepositoryImpl implements AnalysisRepository {
    private final MoodRepository moodRepository;
    private final RuleBasedSentimentAnalyzer analyzer;
    private final AiAnalysisService aiAnalysisService;

    public AnalysisRepositoryImpl(MoodRepository moodRepository, RuleBasedSentimentAnalyzer analyzer) {
        this(moodRepository, analyzer, new AiAnalysisService());
    }

    public AnalysisRepositoryImpl(
            MoodRepository moodRepository,
            RuleBasedSentimentAnalyzer analyzer,
            AiAnalysisService aiAnalysisService
    ) {
        this.moodRepository = moodRepository;
        this.analyzer = analyzer;
        this.aiAnalysisService = aiAnalysisService;
    }

    @Override
    public AnalysisReport generateReport(int days) {
        List<MoodRecord> records = moodRepository.getRecent(days);
        int positive = 0;
        int neutral = 0;
        int negative = 0;
        Map<String, Integer> tagFrequency = new HashMap<>();

        for (MoodRecord record : records) {
            String label = analyzer.analyzeLabel(record.moodType, record.diaryText, record.moodIntensity);
            if ("positive".equals(label)) {
                positive++;
            } else if ("negative".equals(label)) {
                negative++;
            } else {
                neutral++;
            }
            for (String tag : record.tags) {
                tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
            }
        }

        String summary = aiAnalysisService.generateSummaryWithFallback(
                days,
                records.size(),
                positive,
                neutral,
                negative,
                tagFrequency
        );
        if (summary == null || summary.trim().isEmpty()) {
            summary = buildSummaryText(days, records.size(), positive, neutral, negative, tagFrequency);
        }
        return new AnalysisReport(records.size(), positive, neutral, negative, tagFrequency, summary);
    }

    private String buildSummaryText(
            int days,
            int total,
            int positive,
            int neutral,
            int negative,
            Map<String, Integer> tagFrequency
    ) {
        if (total == 0) {
            return "No mood records in the last " + days + " days.";
        }
        String dominantTag = mostFrequentTag(tagFrequency);
        return "In the last " + days + " days: "
                + positive + " positive, "
                + neutral + " neutral, "
                + negative + " negative records."
                + (dominantTag.isEmpty() ? "" : " Most frequent tag: " + dominantTag + ".");
    }

    private String mostFrequentTag(Map<String, Integer> tagFrequency) {
        String bestTag = "";
        int bestCount = 0;
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestTag = entry.getKey();
                bestCount = entry.getValue();
            }
        }
        return bestTag;
    }
}
