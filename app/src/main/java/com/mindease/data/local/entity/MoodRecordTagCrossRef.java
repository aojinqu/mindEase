package com.mindease.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(
        tableName = "mood_record_tag_cross_ref",
        primaryKeys = {"record_id", "tag_id"}
)
public class MoodRecordTagCrossRef {
    @NonNull
    @ColumnInfo(name = "record_id")
    public String recordId;

    @NonNull
    @ColumnInfo(name = "tag_id")
    public String tagId;
}
