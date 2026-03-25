package com.mindease.domain.usecase;

import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.repository.MoodRepository;

import java.util.List;

public class GetRecentMoodRecordsUseCase {
    private final MoodRepository repository;

    public GetRecentMoodRecordsUseCase(MoodRepository repository) {
        this.repository = repository;
    }

    public List<MoodRecord> execute(int days) {
        return repository.getRecent(days);
    }
}
