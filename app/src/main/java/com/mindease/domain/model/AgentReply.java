package com.mindease.domain.model;

public class AgentReply {
    public final String text;
    public final String modelName;
    public final String providerName;
    public final String status;
    public final String diagnosticMessage;

    public AgentReply(String text, String modelName, String providerName, String status, String diagnosticMessage) {
        this.text = text;
        this.modelName = modelName;
        this.providerName = providerName;
        this.status = status;
        this.diagnosticMessage = diagnosticMessage;
    }
}
