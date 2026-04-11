package com.mindease.domain.usecase;

import com.mindease.domain.model.AgentSession;
import com.mindease.domain.repository.AgentRepository;

public class StartAgentSessionUseCase {
    private final AgentRepository agentRepository;

    public StartAgentSessionUseCase(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public AgentSession execute(String title) {
        return agentRepository.startSession(title);
    }
}
