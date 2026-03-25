package com.mindease.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mood_records")
public class MoodRecordEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "record_id")
    public String recordId;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "mood_type")
    public String moodType;

    @ColumnInfo(name = "mood_intensity")
    public int moodIntensity;

    @ColumnInfo(name = "diary_text")
    public String diaryText;

    @ColumnInfo(name = "created_at")
    public long createdAt;
}
