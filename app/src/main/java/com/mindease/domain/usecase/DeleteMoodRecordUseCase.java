package com.mindease.domain.usecase;

import com.mindease.domain.repository.MoodRepository;

public class DeleteMoodRecordUseCase {
    private final MoodRepository repository;

    public DeleteMoodRecordUseCase(MoodRepository repository) {
        this.repository = repository;
    }

    public void execute(String recordId) {
        repository.delete(recordId);
    }
}
