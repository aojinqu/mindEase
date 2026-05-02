package com.mindease.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mindease.domain.service.RuleBasedSentimentAnalyzer;

import org.junit.Test;

public class RuleBasedSentimentAnalyzerTest {

    @Test
    public void analyzeLabel_positiveText_shouldBePositive() {
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();
        String label = analyzer.analyzeLabel("I feel good and hopeful today", 4);
        assertEquals("positive", label);
    }

    @Test
    public void analyzeLabel_negativeText_shouldBeNegative() {
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();
        String label = analyzer.analyzeLabel("I feel stressed and lonely", 2);
        assertEquals("negative", label);
    }

    @Test
    public void analyzeScore_emptyText_shouldStillUseIntensity() {
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();
        float score = analyzer.analyzeScore("", 5);
        assertTrue(score > 0f);
    }

    @Test
    public void analyzeLabel_happyMoodType_shouldBePositive() {
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();
        String label = analyzer.analyzeLabel("Happy", "", 3);
        assertEquals("positive", label);
    }

    @Test
    public void analyzeLabel_anxiousMoodType_shouldBeNegative() {
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();
        String label = analyzer.analyzeLabel("Anxious", "", 3);
        assertEquals("negative", label);
    }

    @Test
    public void analyzeLabel_calmMoodType_shouldBeNeutral() {
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();
        String label = analyzer.analyzeLabel("Calm", "", 3);
        assertEquals("neutral", label);
    }

    @Test
    public void analyzeLabel_synonymMoodTypes_shouldBeMapped() {
        RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();

        assertEquals("positive", analyzer.analyzeLabel("Cheerful", "", 3));
        assertEquals("positive", analyzer.analyzeLabel("Peaceful", "", 3));
        assertEquals("neutral", analyzer.analyzeLabel("Balanced", "", 3));
        assertEquals("negative", analyzer.analyzeLabel("Overwhelmed", "", 3));
        assertEquals("negative", analyzer.analyzeLabel("Worried", "", 3));
    }
}
