package com.mindease.domain.usecase;

import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.repository.MoodRepository;

public class CreateMoodRecordUseCase {
    private final MoodRepository repository;

    public CreateMoodRecordUseCase(MoodRepository repository) {
        this.repository = repository;
    }

    public void execute(MoodRecord record) {
        repository.create(record);
    }
}
