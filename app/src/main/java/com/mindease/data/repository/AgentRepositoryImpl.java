package com.mindease.data.repository;

import com.mindease.data.local.dao.AgentMessageDao;
import com.mindease.data.local.dao.AgentSessionDao;
import com.mindease.data.local.entity.AgentMessageEntity;
import com.mindease.data.local.entity.AgentSessionEntity;
import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.model.AgentPromptContext;
import com.mindease.domain.model.AgentReply;
import com.mindease.domain.model.AgentSession;
import com.mindease.domain.model.RiskAssessment;
import com.mindease.domain.repository.AgentRepository;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.PromptContextBuilder;
import com.mindease.domain.service.RiskGuardService;
import com.mindease.domain.service.SystemTimeProvider;
import com.mindease.domain.service.TherapyAgentService;
import com.mindease.domain.service.TimeProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AgentRepositoryImpl implements AgentRepository {
    private final AgentSessionDao agentSessionDao;
    private final AgentMessageDao agentMessageDao;
    private final UserRepository userRepository;
    private final PromptContextBuilder promptContextBuilder;
    private final RiskGuardService riskGuardService;
    private final TherapyAgentService therapyAgentService;
    private final TimeProvider timeProvider;

    public AgentRepositoryImpl(
            AgentSessionDao agentSessionDao,
            AgentMessageDao agentMessageDao,
            UserRepository userRepository,
            PromptContextBuilder promptContextBuilder,
            RiskGuardService riskGuardService,
            TherapyAgentService therapyAgentService
    ) {
        this(
                agentSessionDao,
                agentMessageDao,
                userRepository,
                promptContextBuilder,
                riskGuardService,
                therapyAgentService,
                new SystemTimeProvider()
        );
    }

    public AgentRepositoryImpl(
            AgentSessionDao agentSessionDao,
            AgentMessageDao agentMessageDao,
            UserRepository userRepository,
            PromptContextBuilder promptContextBuilder,
            RiskGuardService riskGuardService,
            TherapyAgentService therapyAgentService,
            TimeProvider timeProvider
    ) {
        this.agentSessionDao = agentSessionDao;
        this.agentMessageDao = agentMessageDao;
        this.userRepository = userRepository;
        this.promptContextBuilder = promptContextBuilder;
        this.riskGuardService = riskGuardService;
        this.therapyAgentService = therapyAgentService;
        this.timeProvider = timeProvider;
    }

    @Override
    public AgentSession startSession(String title) {
        long now = timeProvider.nowMillis();
        AgentSessionEntity entity = new AgentSessionEntity();
        entity.sessionId = UUID.randomUUID().toString();
        entity.userId = userRepository.currentUserId();
        entity.title = safeTitle(title);
        entity.latestContextSummary = "";
        entity.modelName = "";
        entity.createdAt = now;
        entity.updatedAt = now;
        agentSessionDao.insert(entity);
        return toDomain(entity);
    }

    @Override
    public AgentMessage sendMessage(String sessionId, String messageText) {
        AgentSessionEntity session = requireSession(sessionId);
        long now = timeProvider.nowMillis();

        AgentMessageEntity userMessage = new AgentMessageEntity();
        userMessage.messageId = UUID.randomUUID().toString();
        userMessage.sessionId = sessionId;
        userMessage.role = "user";
        userMessage.messageText = messageText == null ? "" : messageText.trim();
        userMessage.promptContextJson = "";
        userMessage.status = "success";
        userMessage.createdAt = now;
        agentMessageDao.insert(userMessage);

        List<AgentMessage> recentMessages = reverse(agentMessageDao.findLatestBySessionId(sessionId, 8));
        AgentPromptContext promptContext = promptContextBuilder.build(recentMessages);
        RiskAssessment riskAssessment = riskGuardService.assess(userMessage.messageText);
        AgentReply reply = therapyAgentService.generateReply(
                userMessage.messageText,
                recentMessages,
                promptContext,
                riskAssessment
        );

        AgentMessageEntity assistantMessage = new AgentMessageEntity();
        assistantMessage.messageId = UUID.randomUUID().toString();
        assistantMessage.sessionId = sessionId;
        assistantMessage.role = "assistant";
        assistantMessage.messageText = reply.text;
        assistantMessage.promptContextJson = attachDiagnostic(promptContext.promptContextJson, reply.diagnosticMessage);
        assistantMessage.status = reply.status;
        assistantMessage.createdAt = timeProvider.nowMillis();
        agentMessageDao.insert(assistantMessage);

        session.title = updateTitleIfNeeded(session.title, userMessage.messageText);
        session.latestContextSummary = promptContext.summaryText;
        session.modelName = reply.modelName;
        session.updatedAt = assistantMessage.createdAt;
        agentSessionDao.update(session);

        return toDomain(assistantMessage);
    }

    @Override
    public List<AgentMessage> getSessionMessages(String sessionId) {
        requireSession(sessionId);
        List<AgentMessageEntity> entities = agentMessageDao.findAllBySessionId(sessionId);
        List<AgentMessage> messages = new ArrayList<>();
        for (AgentMessageEntity entity : entities) {
            messages.add(toDomain(entity));
        }
        return messages;
    }

    @Override
    public List<AgentSession> listSessions() {
        List<AgentSessionEntity> entities = agentSessionDao.findAllByUserId(userRepository.currentUserId());
        List<AgentSession> sessions = new ArrayList<>();
        for (AgentSessionEntity entity : entities) {
            sessions.add(toDomain(entity));
        }
        return sessions;
    }

    private AgentSessionEntity requireSession(String sessionId) {
        AgentSessionEntity entity = agentSessionDao.findByIdAndUserId(sessionId, userRepository.currentUserId());
        if (entity == null) {
            throw new IllegalArgumentException("Agent session not found: " + sessionId);
        }
        return entity;
    }

    private List<AgentMessage> reverse(List<AgentMessageEntity> entities) {
        List<AgentMessage> messages = new ArrayList<>();
        for (AgentMessageEntity entity : entities) {
            messages.add(toDomain(entity));
        }
        Collections.reverse(messages);
        return messages;
    }

    private AgentSession toDomain(AgentSessionEntity entity) {
        return new AgentSession(
                entity.sessionId,
                entity.title,
                entity.latestContextSummary,
                entity.modelName,
                entity.createdAt,
                entity.updatedAt
        );
    }

    private AgentMessage toDomain(AgentMessageEntity entity) {
        return new AgentMessage(
                entity.messageId,
                entity.sessionId,
                entity.role,
                entity.messageText,
                entity.promptContextJson,
                entity.status,
                entity.createdAt
        );
    }

    private String safeTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "New chat";
        }
        return title.trim();
    }

    private String updateTitleIfNeeded(String currentTitle, String firstUserMessage) {
        if (currentTitle != null && !"New chat".equals(currentTitle)) {
            return currentTitle;
        }
        if (firstUserMessage == null || firstUserMessage.trim().isEmpty()) {
            return "New chat";
        }
        String trimmed = firstUserMessage.trim();
        return trimmed.length() <= 24 ? trimmed : trimmed.substring(0, 24) + "...";
    }

    private String attachDiagnostic(String promptContextJson, String diagnosticMessage) {
        if (diagnosticMessage == null || diagnosticMessage.trim().isEmpty()) {
            return promptContextJson;
        }
        String safeJson = promptContextJson == null || promptContextJson.trim().isEmpty()
                ? "{}"
                : promptContextJson.trim();
        String escapedDiagnostic = diagnosticMessage
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        if ("{}".equals(safeJson)) {
            return "{\"diagnostic\":\"" + escapedDiagnostic + "\"}";
        }
        if (safeJson.endsWith("}")) {
            return safeJson.substring(0, safeJson.length() - 1)
                    + ",\"diagnostic\":\"" + escapedDiagnostic + "\"}";
        }
        return safeJson;
    }
}
