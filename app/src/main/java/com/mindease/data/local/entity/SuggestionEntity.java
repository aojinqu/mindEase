package com.mindease.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "suggestions")
public class SuggestionEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "suggestion_id")
    public String suggestionId;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "suggestion_type")
    public String suggestionType;

    @ColumnInfo(name = "suggestion_text")
    public String suggestionText;

    @ColumnInfo(name = "generated_at")
    public long generatedAt;
}
