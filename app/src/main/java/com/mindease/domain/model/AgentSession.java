package com.mindease.domain.model;

public class AgentSession {
    public final String id;
    public final String title;
    public final String latestContextSummary;
    public final String modelName;
    public final long createdAt;
    public final long updatedAt;

    public AgentSession(
            String id,
            String title,
            String latestContextSummary,
            String modelName,
            long createdAt,
            long updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.latestContextSummary = latestContextSummary;
        this.modelName = modelName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
