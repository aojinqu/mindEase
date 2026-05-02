package com.mindease.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mindease.data.repository.AnalysisRepositoryImpl;
import com.mindease.data.repository.MoodRepositoryImpl;
import com.mindease.data.repository.SuggestionRepositoryImpl;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.model.Suggestion;
import com.mindease.domain.service.RuleBasedSentimentAnalyzer;
import com.mindease.domain.service.SuggestionEngine;
import com.mindease.domain.service.TimeProvider;
import com.mindease.domain.usecase.CreateMoodRecordUseCase;
import com.mindease.domain.usecase.GenerateMoodAnalysisUseCase;
import com.mindease.domain.usecase.GenerateSuggestionUseCase;

import org.junit.Test;

import java.util.Arrays;

public class CoreFlowUseCaseTest {

    @Test
    public void createAnalyzeSuggest_shouldProduceExpectedResult() {
        long now = 2_000_000L;
        MoodRepositoryImpl moodRepository = new MoodRepositoryImpl(new FixedTimeProvider(now));
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();
        AnalysisRepositoryImpl analysisRepository = new AnalysisRepositoryImpl(moodRepository, analyzer);
        SuggestionRepositoryImpl suggestionRepository = new SuggestionRepositoryImpl();

        CreateMoodRecordUseCase createMoodRecordUseCase = new CreateMoodRecordUseCase(moodRepository);
        GenerateMoodAnalysisUseCase analysisUseCase = new GenerateMoodAnalysisUseCase(analysisRepository);
        GenerateSuggestionUseCase suggestionUseCase = new GenerateSuggestionUseCase(
                suggestionRepository,
                new SuggestionEngine()
        );

        createMoodRecordUseCase.execute(record("r1", 2, "I am stressed for exam", now - daysToMillis(2), "exam"));
        createMoodRecordUseCase.execute(record("r2", 2, "Feeling lonely and tired", now - daysToMillis(1), "sleep"));
        createMoodRecordUseCase.execute(record("r3", 3, "Bad mood today", now - daysToMillis(1), "study"));
        createMoodRecordUseCase.execute(record("r4", 4, "good progress", now - daysToMillis(1), "study"));

        AnalysisReport report = analysisUseCase.execute(7);
        assertEquals(4, report.totalCount);
        assertEquals(3, report.negativeCount);
        assertTrue(report.summaryText.contains("7 days"));
        assertEquals(Integer.valueOf(2), report.tagFrequency.get("study"));

        Suggestion suggestion = suggestionUseCase.execute(report);
        assertEquals("stress_relief", suggestion.type);
        assertEquals(suggestion.id, suggestionRepository.latest().id);
    }

    @Test
    public void analyzeReport_shouldClassifyMoodTypesByMappingTable() {
        long now = 2_000_000L;
        MoodRepositoryImpl moodRepository = new MoodRepositoryImpl(new FixedTimeProvider(now));
        AnalysisRepositoryImpl analysisRepository = new AnalysisRepositoryImpl(
                moodRepository,
                new RuleBasedSentimentAnalyzer()
        );

        CreateMoodRecordUseCase createMoodRecordUseCase = new CreateMoodRecordUseCase(moodRepository);
        createMoodRecordUseCase.execute(record("r1", "Happy", 3, "", now - daysToMillis(1), "study"));
        createMoodRecordUseCase.execute(record("r2", "Calm", 3, "", now - daysToMillis(1), "sleep"));
        createMoodRecordUseCase.execute(record("r3", "Anxious", 3, "", now - daysToMillis(1), "exam"));
        createMoodRecordUseCase.execute(record("r4", "Overwhelmed", 3, "", now - daysToMillis(1), "exam"));

        AnalysisReport report = new GenerateMoodAnalysisUseCase(analysisRepository).execute(7);

        assertEquals(4, report.totalCount);
        assertEquals(1, report.positiveCount);
        assertEquals(1, report.neutralCount);
        assertEquals(2, report.negativeCount);
        assertTrue(report.summaryText.contains("1 positive"));
        assertTrue(report.summaryText.contains("1 neutral"));
        assertTrue(report.summaryText.contains("2 negative"));
    }

    private MoodRecord record(String id, int intensity, String text, long createdAt, String tag) {
        return new MoodRecord(id, "mood", intensity, text, Arrays.asList(tag), createdAt);
    }

    private MoodRecord record(String id, String moodType, int intensity, String text, long createdAt, String tag) {
        return new MoodRecord(id, moodType, intensity, text, Arrays.asList(tag), createdAt);
    }

    private long daysToMillis(int days) {
        return days * 24L * 60L * 60L * 1000L;
    }

    private static class FixedTimeProvider implements TimeProvider {
        private final long now;

        private FixedTimeProvider(long now) {
            this.now = now;
        }

        @Override
        public long nowMillis() {
            return now;
        }
    }
}
