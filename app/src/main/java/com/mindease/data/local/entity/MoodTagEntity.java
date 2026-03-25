package com.mindease.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mood_tags")
public class MoodTagEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tag_id")
    public String tagId;

    @ColumnInfo(name = "tag_name")
    public String tagName;

    @ColumnInfo(name = "tag_category")
    public String tagCategory;
}
