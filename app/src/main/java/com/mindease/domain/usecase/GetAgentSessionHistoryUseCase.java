package com.mindease.domain.usecase;

import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.repository.AgentRepository;

import java.util.List;

public class GetAgentSessionHistoryUseCase {
    private final AgentRepository agentRepository;

    public GetAgentSessionHistoryUseCase(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public List<AgentMessage> execute(String sessionId) {
        return agentRepository.getSessionMessages(sessionId);
    }
}
