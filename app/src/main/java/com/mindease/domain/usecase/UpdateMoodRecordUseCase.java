package com.mindease.domain.usecase;

import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.repository.MoodRepository;

public class UpdateMoodRecordUseCase {
    private final MoodRepository repository;

    public UpdateMoodRecordUseCase(MoodRepository repository) {
        this.repository = repository;
    }

    public void execute(MoodRecord record) {
        repository.update(record);
    }
}
