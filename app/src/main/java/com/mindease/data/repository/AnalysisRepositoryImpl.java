package com.mindease.data.repository;

import com.mindease.domain.repository.AnalysisRepository;

public class AnalysisRepositoryImpl implements AnalysisRepository {
    @Override
    public String buildSummary(int days) {
        return "Analysis summary placeholder for last " + days + " days.";
    }
}
