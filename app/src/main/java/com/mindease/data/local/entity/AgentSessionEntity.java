package com.mindease.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "agent_sessions")
public class AgentSessionEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "session_id")
    public String sessionId;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "latest_context_summary")
    public String latestContextSummary;

    @ColumnInfo(name = "model_name")
    public String modelName;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;
}
