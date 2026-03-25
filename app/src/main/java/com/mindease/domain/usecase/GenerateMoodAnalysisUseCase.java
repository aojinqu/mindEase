package com.mindease.domain.usecase;

import com.mindease.domain.repository.AnalysisRepository;

public class GenerateMoodAnalysisUseCase {
    private final AnalysisRepository repository;

    public GenerateMoodAnalysisUseCase(AnalysisRepository repository) {
        this.repository = repository;
    }

    public String execute(int days) {
        return repository.buildSummary(days);
    }
}
