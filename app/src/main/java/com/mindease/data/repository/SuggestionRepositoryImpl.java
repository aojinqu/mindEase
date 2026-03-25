package com.mindease.data.repository;

import com.mindease.domain.model.Suggestion;
import com.mindease.domain.repository.SuggestionRepository;

public class SuggestionRepositoryImpl implements SuggestionRepository {
    @Override
    public void save(Suggestion suggestion) {
        // Skeleton: connect to local Room suggestions table.
    }

    @Override
    public Suggestion latest() {
        return new Suggestion("local-placeholder", "Take a short mindful break.", "relax");
    }
}
