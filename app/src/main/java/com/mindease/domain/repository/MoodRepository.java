package com.mindease.domain.repository;

import com.mindease.domain.model.MoodRecord;

import java.util.List;

public interface MoodRepository {
    void create(MoodRecord record);
    void update(MoodRecord record);
    void delete(String recordId);
    List<MoodRecord> getRecent(int days);
}
