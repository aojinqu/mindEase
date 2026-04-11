package com.mindease.domain.usecase;

import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.repository.AgentRepository;

public class SendAgentMessageUseCase {
    private final AgentRepository agentRepository;

    public SendAgentMessageUseCase(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public AgentMessage execute(String sessionId, String messageText) {
        return agentRepository.sendMessage(sessionId, messageText);
    }
}
