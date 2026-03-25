package com.mindease.data.repository;

import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.repository.MoodRepository;

import java.util.ArrayList;
import java.util.List;

public class MoodRepositoryImpl implements MoodRepository {
    @Override
    public void create(MoodRecord record) {
        // Skeleton: persist record with Room.
    }

    @Override
    public void update(MoodRecord record) {
        // Skeleton: update existing record.
    }

    @Override
    public void delete(String recordId) {
        // Skeleton: delete by id.
    }

    @Override
    public List<MoodRecord> getRecent(int days) {
        return new ArrayList<>();
    }
}
