package com.mindease.domain.model;

public class AgentMessage {
    public final String id;
    public final String sessionId;
    public final String role;
    public final String text;
    public final String promptContextJson;
    public final String status;
    public final long createdAt;

    public AgentMessage(
            String id,
            String sessionId,
            String role,
            String text,
            String promptContextJson,
            String status,
            long createdAt
    ) {
        this.id = id;
        this.sessionId = sessionId;
        this.role = role;
        this.text = text;
        this.promptContextJson = promptContextJson;
        this.status = status;
        this.createdAt = createdAt;
    }
}
