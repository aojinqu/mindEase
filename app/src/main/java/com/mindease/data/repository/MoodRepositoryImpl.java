package com.mindease.data.repository;

import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.repository.MoodRepository;
import com.mindease.domain.service.SystemTimeProvider;
import com.mindease.domain.service.TimeProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoodRepositoryImpl implements MoodRepository {
    private final Map<String, MoodRecord> records = new HashMap<>();
    private final TimeProvider timeProvider;

    public MoodRepositoryImpl() {
        this(new SystemTimeProvider());
    }

    public MoodRepositoryImpl(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public void create(MoodRecord record) {
        records.put(record.id, record);
    }

    @Override
    public void update(MoodRecord record) {
        if (!records.containsKey(record.id)) {
            throw new IllegalArgumentException("Mood record not found: " + record.id);
        }
        records.put(record.id, record);
    }

    @Override
    public void delete(String recordId) {
        records.remove(recordId);
    }

    @Override
    public List<MoodRecord> getRecent(int days) {
        long now = timeProvider.nowMillis();
        long from = now - (days * 24L * 60L * 60L * 1000L);
        List<MoodRecord> result = new ArrayList<>();
        for (MoodRecord record : records.values()) {
            if (record.createdAt >= from) {
                result.add(record);
            }
        }
        result.sort(Comparator.comparingLong((MoodRecord r) -> r.createdAt).reversed());
        return result;
    }
}
