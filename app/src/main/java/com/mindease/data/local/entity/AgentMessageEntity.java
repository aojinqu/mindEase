package com.mindease.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "agent_messages")
public class AgentMessageEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "message_id")
    public String messageId;

    @ColumnInfo(name = "session_id")
    public String sessionId;

    @ColumnInfo(name = "role")
    public String role;

    @ColumnInfo(name = "message_text")
    public String messageText;

    @ColumnInfo(name = "prompt_context_json")
    public String promptContextJson;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "created_at")
    public long createdAt;
}
