package com.mindease.domain.repository;

import com.mindease.domain.model.Suggestion;

public interface SuggestionRepository {
    void save(Suggestion suggestion);
    Suggestion latest();
}
