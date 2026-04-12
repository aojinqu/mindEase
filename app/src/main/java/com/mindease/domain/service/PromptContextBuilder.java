package com.mindease.domain.service;

import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.model.AgentPromptContext;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.model.Suggestion;
import com.mindease.domain.repository.AnalysisRepository;
import com.mindease.domain.repository.MoodRepository;
import com.mindease.domain.repository.SuggestionRepository;

import java.util.List;
import java.util.Map;

public class PromptContextBuilder {
    private final MoodRepository moodRepository;
    private final AnalysisRepository analysisRepository;
    private final SuggestionRepository suggestionRepository;

    public PromptContextBuilder(
            MoodRepository moodRepository,
            AnalysisRepository analysisRepository,
            SuggestionRepository suggestionRepository
    ) {
        this.moodRepository = moodRepository;
        this.analysisRepository = analysisRepository;
        this.suggestionRepository = suggestionRepository;
    }

    public AgentPromptContext build(List<AgentMessage> recentMessages) {
        List<MoodRecord> recentRecords = moodRepository.getRecent(7);
        AnalysisReport report = analysisRepository.generateReport(7);
        Suggestion suggestion = suggestionRepository.latest();

        String latestMood = summarizeLatestMood(recentRecords);
        String sevenDayTrend = summarizeSevenDayTrend(report);
        String topTags = summarizeTopTags(report.tagFrequency);
        String recentMoodTimeline = summarizeRecentMoodTimeline(recentRecords);
        String summary = "Latest mood: " + latestMood
                + " | 7-day trend: " + sevenDayTrend
                + " | Top tags: " + topTags
                + " | Recent moods: " + recentMoodTimeline
                + " | Latest suggestion: " + suggestion.text;

        String promptContextJson = "{"
                + "\"latestMood\":\"" + escapeJson(latestMood) + "\","
                + "\"sevenDaySummary\":\"" + escapeJson(sevenDayTrend) + "\","
                + "\"topTags\":\"" + escapeJson(topTags) + "\","
                + "\"recentMoodTimeline\":\"" + escapeJson(recentMoodTimeline) + "\","
                + "\"latestSuggestion\":\"" + escapeJson(suggestion.text) + "\","
                + "\"recentChatSummary\":\"" + escapeJson(summarizeRecentMessages(recentMessages)) + "\""
                + "}";

        return new AgentPromptContext(summary, promptContextJson);
    }

    private String summarizeLatestMood(List<MoodRecord> records) {
        if (records == null || records.isEmpty()) {
            return "No recent mood records";
        }
        MoodRecord latest = records.get(0);
        String tags = latest.tags == null || latest.tags.isEmpty() ? "no tags" : String.join(", ", latest.tags);
        String diary = latest.diaryText == null || latest.diaryText.trim().isEmpty()
                ? "no diary text"
                : latest.diaryText.trim();
        return latest.moodType + " (intensity " + latest.moodIntensity + "), tags: " + tags + ", diary: " + diary;
    }

    private String summarizeTopTags(Map<String, Integer> tagFrequency) {
        if (tagFrequency == null || tagFrequency.isEmpty()) {
            return "none";
        }
        String bestTag = "none";
        String secondTag = null;
        int bestCount = 0;
        int secondCount = 0;
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            if (entry.getValue() > bestCount) {
                secondTag = bestTag;
                secondCount = bestCount;
                bestTag = entry.getKey();
                bestCount = entry.getValue();
            } else if (entry.getValue() > secondCount) {
                secondTag = entry.getKey();
                secondCount = entry.getValue();
            }
        }
        if (secondTag == null || "none".equals(secondTag)) {
            return bestTag + " (" + bestCount + ")";
        }
        return bestTag + " (" + bestCount + "), " + secondTag + " (" + secondCount + ")";
    }

    private String summarizeSevenDayTrend(AnalysisReport report) {
        return "positive " + report.positiveCount
                + ", neutral " + report.neutralCount
                + ", negative " + report.negativeCount
                + "; " + report.summaryText;
    }

    private String summarizeRecentMoodTimeline(List<MoodRecord> records) {
        if (records == null || records.isEmpty()) {
            return "no recent moods";
        }
        StringBuilder builder = new StringBuilder();
        int limit = Math.min(3, records.size());
        for (int i = 0; i < limit; i++) {
            MoodRecord record = records.get(i);
            if (i > 0) {
                builder.append(" | ");
            }
            String tags = record.tags == null || record.tags.isEmpty() ? "no tags" : String.join(", ", record.tags);
            builder.append(record.moodType)
                    .append(" (")
                    .append(record.moodIntensity)
                    .append("/5, ")
                    .append(tags)
                    .append(")");
        }
        return builder.toString();
    }

    private String summarizeRecentMessages(List<AgentMessage> recentMessages) {
        if (recentMessages == null || recentMessages.isEmpty()) {
            return "No recent chat history";
        }
        StringBuilder builder = new StringBuilder();
        int start = Math.max(0, recentMessages.size() - 4);
        for (int i = start; i < recentMessages.size(); i++) {
            AgentMessage message = recentMessages.get(i);
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(message.role).append(": ").append(truncate(message.text, 80));
        }
        return builder.toString();
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength) + "...";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
