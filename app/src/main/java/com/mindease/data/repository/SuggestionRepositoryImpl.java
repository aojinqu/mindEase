package com.mindease.data.repository;

import com.mindease.domain.model.Suggestion;
import com.mindease.domain.repository.SuggestionRepository;

public class SuggestionRepositoryImpl implements SuggestionRepository {
    private Suggestion latestSuggestion;

    @Override
    public void save(Suggestion suggestion) {
        latestSuggestion = suggestion;
    }

    @Override
    public Suggestion latest() {
        if (latestSuggestion == null) {
            latestSuggestion = new Suggestion(
                    "local-placeholder",
                    "Take a short mindful break.",
                    "relax"
            );
        }
        return latestSuggestion;
    }
}
