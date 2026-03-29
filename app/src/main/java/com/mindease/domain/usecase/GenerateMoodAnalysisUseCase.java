package com.mindease.domain.usecase;

import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.repository.AnalysisRepository;

public class GenerateMoodAnalysisUseCase {
    private final AnalysisRepository repository;

    public GenerateMoodAnalysisUseCase(AnalysisRepository repository) {
        this.repository = repository;
    }

    public AnalysisReport execute(int days) {
        return repository.generateReport(days);
    }
}
