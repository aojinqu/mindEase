package com.mindease.domain.repository;

import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.model.AgentSession;

import java.util.List;

public interface AgentRepository {
    AgentSession startSession(String title);

    AgentMessage sendMessage(String sessionId, String messageText);

    List<AgentMessage> getSessionMessages(String sessionId);

    List<AgentSession> listSessions();
}
