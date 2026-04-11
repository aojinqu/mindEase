package com.mindease.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mindease.data.local.dao.AgentMessageDao;
import com.mindease.data.local.dao.AgentSessionDao;
import com.mindease.data.local.entity.AgentMessageEntity;
import com.mindease.data.local.entity.AgentSessionEntity;
import com.mindease.data.repository.AgentRepositoryImpl;
import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.model.AgentPromptContext;
import com.mindease.domain.model.AgentSession;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.model.Suggestion;
import com.mindease.domain.repository.AnalysisRepository;
import com.mindease.domain.repository.MoodRepository;
import com.mindease.domain.repository.SuggestionRepository;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.PromptContextBuilder;
import com.mindease.domain.service.RiskGuardService;
import com.mindease.domain.service.TherapyAgentService;
import com.mindease.domain.service.TimeProvider;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentRepositoryImplTest {

    @Test
    public void sendMessage_whenRemoteFails_shouldPersistFallbackReply() {
        InMemoryAgentSessionDao sessionDao = new InMemoryAgentSessionDao();
        InMemoryAgentMessageDao messageDao = new InMemoryAgentMessageDao();
        PromptContextBuilder promptContextBuilder = new PromptContextBuilder(
                new FakeMoodRepository(),
                new FakeAnalysisRepository(),
                new FakeSuggestionRepository()
        );

        AgentRepositoryImpl repository = new AgentRepositoryImpl(
                sessionDao,
                messageDao,
                new FixedUserRepository(),
                promptContextBuilder,
                new RiskGuardService(),
                new AlwaysFailTherapyAgentService(),
                new FixedTimeProvider(10_000L)
        );

        AgentSession session = repository.startSession(" ");
        AgentMessage reply = repository.sendMessage(session.id, "I feel stressed because of exams");
        List<AgentMessage> history = repository.getSessionMessages(session.id);

        assertEquals("assistant", reply.role);
        assertTrue(reply.status.contains("fallback"));
        assertEquals(2, history.size());
        assertFalse(history.get(1).promptContextJson.isEmpty());
        assertTrue(repository.listSessions().get(0).latestContextSummary.contains("7-day trend"));
        assertTrue(repository.listSessions().get(0).title.startsWith("I feel stressed"));
    }

    private static class InMemoryAgentSessionDao implements AgentSessionDao {
        private final Map<String, AgentSessionEntity> sessions = new HashMap<>();

        @Override
        public void insert(AgentSessionEntity entity) {
            sessions.put(entity.sessionId, copy(entity));
        }

        @Override
        public void update(AgentSessionEntity entity) {
            sessions.put(entity.sessionId, copy(entity));
        }

        @Override
        public AgentSessionEntity findByIdAndUserId(String sessionId, String userId) {
            AgentSessionEntity entity = sessions.get(sessionId);
            if (entity == null || !userId.equals(entity.userId)) {
                return null;
            }
            return copy(entity);
        }

        @Override
        public List<AgentSessionEntity> findAllByUserId(String userId) {
            List<AgentSessionEntity> result = new ArrayList<>();
            for (AgentSessionEntity entity : sessions.values()) {
                if (userId.equals(entity.userId)) {
                    result.add(copy(entity));
                }
            }
            result.sort((left, right) -> Long.compare(right.updatedAt, left.updatedAt));
            return result;
        }

        private AgentSessionEntity copy(AgentSessionEntity entity) {
            AgentSessionEntity copy = new AgentSessionEntity();
            copy.sessionId = entity.sessionId;
            copy.userId = entity.userId;
            copy.title = entity.title;
            copy.latestContextSummary = entity.latestContextSummary;
            copy.modelName = entity.modelName;
            copy.createdAt = entity.createdAt;
            copy.updatedAt = entity.updatedAt;
            return copy;
        }
    }

    private static class InMemoryAgentMessageDao implements AgentMessageDao {
        private final List<AgentMessageEntity> messages = new ArrayList<>();

        @Override
        public void insert(AgentMessageEntity entity) {
            messages.add(copy(entity));
        }

        @Override
        public List<AgentMessageEntity> findAllBySessionId(String sessionId) {
            List<AgentMessageEntity> result = new ArrayList<>();
            for (AgentMessageEntity entity : messages) {
                if (sessionId.equals(entity.sessionId)) {
                    result.add(copy(entity));
                }
            }
            result.sort((left, right) -> Long.compare(left.createdAt, right.createdAt));
            return result;
        }

        @Override
        public List<AgentMessageEntity> findLatestBySessionId(String sessionId, int limit) {
            List<AgentMessageEntity> all = findAllBySessionId(sessionId);
            Collections.reverse(all);
            if (all.size() > limit) {
                return new ArrayList<>(all.subList(0, limit));
            }
            return all;
        }

        private AgentMessageEntity copy(AgentMessageEntity entity) {
            AgentMessageEntity copy = new AgentMessageEntity();
            copy.messageId = entity.messageId;
            copy.sessionId = entity.sessionId;
            copy.role = entity.role;
            copy.messageText = entity.messageText;
            copy.promptContextJson = entity.promptContextJson;
            copy.status = entity.status;
            copy.createdAt = entity.createdAt;
            return copy;
        }
    }

    private static class FakeMoodRepository implements MoodRepository {
        @Override
        public void create(MoodRecord record) {
        }

        @Override
        public void update(MoodRecord record) {
        }

        @Override
        public void delete(String recordId) {
        }

        @Override
        public List<MoodRecord> getRecent(int days) {
            return Arrays.asList(
                    new MoodRecord("r1", "anxious", 2, "Very stressed about exams", Arrays.asList("exam", "sleep"), 1L)
            );
        }
    }

    private static class FakeAnalysisRepository implements AnalysisRepository {
        @Override
        public AnalysisReport generateReport(int days) {
            Map<String, Integer> tags = new HashMap<>();
            tags.put("exam", 2);
            return new AnalysisReport(3, 0, 1, 2, tags, "The recent trend shows stress clustering around study pressure.");
        }
    }

    private static class FakeSuggestionRepository implements SuggestionRepository {
        @Override
        public void save(Suggestion suggestion) {
        }

        @Override
        public Suggestion latest() {
            return new Suggestion("s1", "Try a 2-minute breathing break tonight.", "stress_relief");
        }
    }

    private static class FixedUserRepository implements UserRepository {
        @Override
        public String currentUserId() {
            return "u_test";
        }
    }

    private static class FixedTimeProvider implements TimeProvider {
        private final long now;

        private FixedTimeProvider(long now) {
            this.now = now;
        }

        @Override
        public long nowMillis() {
            return now;
        }
    }

    private static class AlwaysFailTherapyAgentService extends TherapyAgentService {
        @Override
        protected String generateByRemote(
                String userMessage,
                List<AgentMessage> recentMessages,
                AgentPromptContext context,
                com.mindease.domain.model.RiskAssessment riskAssessment
        ) throws IOException {
            throw new IOException("Simulated network failure");
        }
    }
}
