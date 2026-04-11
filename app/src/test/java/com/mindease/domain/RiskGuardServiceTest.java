package com.mindease.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mindease.domain.model.RiskAssessment;
import com.mindease.domain.service.RiskGuardService;

import org.junit.Test;

public class RiskGuardServiceTest {

    @Test
    public void highRiskMessage_shouldReturnGuidance() {
        RiskGuardService service = new RiskGuardService();

        RiskAssessment result = service.assess("最近我真的不想活了");

        assertTrue(result.highRisk);
        assertTrue(result.guidanceText.contains("emergency"));
    }

    @Test
    public void normalMessage_shouldNotReturnGuidance() {
        RiskGuardService service = new RiskGuardService();

        RiskAssessment result = service.assess("I feel stressed about exams this week");

        assertFalse(result.highRisk);
    }
}
