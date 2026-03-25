package com.mindease.domain.usecase;

import com.mindease.domain.model.Suggestion;
import com.mindease.domain.repository.SuggestionRepository;

public class GenerateSuggestionUseCase {
    private final SuggestionRepository repository;

    public GenerateSuggestionUseCase(SuggestionRepository repository) {
        this.repository = repository;
    }

    public Suggestion execute() {
        return repository.latest();
    }
}
