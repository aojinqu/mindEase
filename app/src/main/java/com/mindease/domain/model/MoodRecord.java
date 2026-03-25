package com.mindease.domain.model;

import java.util.List;

public class MoodRecord {
    public final String id;
    public final String moodType;
    public final int moodIntensity;
    public final String diaryText;
    public final List<String> tags;
    public final long createdAt;

    public MoodRecord(
            String id,
            String moodType,
            int moodIntensity,
            String diaryText,
            List<String> tags,
            long createdAt
    ) {
        this.id = id;
        this.moodType = moodType;
        this.moodIntensity = moodIntensity;
        this.diaryText = diaryText;
        this.tags = tags;
        this.createdAt = createdAt;
    }
}
