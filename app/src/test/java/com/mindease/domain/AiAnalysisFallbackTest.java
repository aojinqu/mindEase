package com.mindease.domain;

import static org.junit.Assert.assertTrue;

import com.mindease.data.repository.AnalysisRepositoryImpl;
import com.mindease.data.repository.MoodRepositoryImpl;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.service.AiAnalysisService;
import com.mindease.domain.service.RuleBasedSentimentAnalyzer;
import com.mindease.domain.service.TimeProvider;

import org.junit.Test;

import java.util.Arrays;

public class AiAnalysisFallbackTest {

    @Test
    public void aiFailure_shouldFallbackToRuleSummary() {
        long now = 3_000_000L;
        MoodRepositoryImpl moodRepository = new MoodRepositoryImpl(new FixedTimeProvider(now));
        moodRepository.create(new MoodRecord(
                "r1",
                "sad",
                2,
                "stressed for exam",
                Arrays.asList("exam"),
                now - daysToMillis(1)
        ));

        AnalysisRepositoryImpl repository = new AnalysisRepositoryImpl(
                moodRepository,
                new RuleBasedSentimentAnalyzer(),
                new AiAnalysisService()
        );

        AnalysisReport report = repository.generateReport(7);
        assertTrue(report.summaryText.contains("7 days"));
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
