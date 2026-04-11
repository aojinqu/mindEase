package com.mindease.domain.model;

public class RiskAssessment {
    public final boolean highRisk;
    public final String guidanceText;

    public RiskAssessment(boolean highRisk, String guidanceText) {
        this.highRisk = highRisk;
        this.guidanceText = guidanceText;
    }
}
