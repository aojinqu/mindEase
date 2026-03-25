package com.mindease.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "analysis_snapshots")
public class AnalysisSnapshotEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "snapshot_id")
    public String snapshotId;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "period_type")
    public String periodType;

    @ColumnInfo(name = "summary_text")
    public String summaryText;

    @ColumnInfo(name = "generated_at")
    public long generatedAt;
}
