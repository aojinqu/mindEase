package com.mindease.domain.usecase;

import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.Suggestion;
import com.mindease.domain.service.SuggestionEngine;
import com.mindease.domain.repository.SuggestionRepository;

public class GenerateSuggestionUseCase {
    private final SuggestionRepository repository;
    private final SuggestionEngine engine;

    public GenerateSuggestionUseCase(SuggestionRepository repository, SuggestionEngine engine) {
        this.repository = repository;
        this.engine = engine;
    }

    public Suggestion execute(AnalysisReport report) {
        Suggestion suggestion = engine.generateFromReport(report);
        repository.save(suggestion);
        return suggestion;
    }
}
