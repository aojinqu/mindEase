package com.mindease.data.repository;

import com.mindease.domain.model.Suggestion;
import com.mindease.domain.repository.SuggestionRepository;
import com.mindease.domain.repository.UserRepository;

public class SuggestionRepositoryImpl implements SuggestionRepository {
    private final java.util.Map<String, Suggestion> suggestionsByUser = new java.util.HashMap<>();
    private final UserRepository userRepository;

    public SuggestionRepositoryImpl() {
        this(new UserRepositoryImpl());
    }

    public SuggestionRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(Suggestion suggestion) {
        suggestionsByUser.put(userRepository.currentUserId(), suggestion);
    }

    @Override
    public Suggestion latest() {
        Suggestion latestSuggestion = suggestionsByUser.get(userRepository.currentUserId());
        if (latestSuggestion == null) {
            latestSuggestion = new Suggestion(
                    "local-placeholder",
                    "Take a short mindful break.",
                    "relax"
            );
            suggestionsByUser.put(userRepository.currentUserId(), latestSuggestion);
        }
        return latestSuggestion;
    }
}
