package com.mindease.domain.model;

public class AgentPromptContext {
    public final String summaryText;
    public final String promptContextJson;

    public AgentPromptContext(String summaryText, String promptContextJson) {
        this.summaryText = summaryText;
        this.promptContextJson = promptContextJson;
    }
}
