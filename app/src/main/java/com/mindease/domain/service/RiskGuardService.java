package com.mindease.domain.service;

import com.mindease.domain.model.RiskAssessment;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RiskGuardService {
    private static final List<String> HIGH_RISK_KEYWORDS = Arrays.asList(
            "suicide",
            "kill myself",
            "self harm",
            "want to die",
            "end my life",
            "不想活",
            "想死",
            "自杀",
            "自残",
            "结束生命"
    );

    public RiskAssessment assess(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return new RiskAssessment(false, "");
        }
        String lower = userMessage.toLowerCase(Locale.US);
        for (String keyword : HIGH_RISK_KEYWORDS) {
            if (lower.contains(keyword.toLowerCase(Locale.US))) {
                return new RiskAssessment(
                        true,
                        "If you may be in immediate danger, contact local emergency services right now. "
                                + "You can also reach a trusted friend, family member, campus counselor, or crisis hotline for immediate human support."
                );
            }
        }
        return new RiskAssessment(false, "");
    }
}
