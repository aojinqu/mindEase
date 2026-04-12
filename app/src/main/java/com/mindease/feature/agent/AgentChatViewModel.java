package com.mindease.feature.agent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.model.AgentSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgentChatViewModel extends ViewModel {
    private final MutableLiveData<AgentChatState> state = new MutableLiveData<>(AgentChatState.idle());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String activeSessionId;

    public LiveData<AgentChatState> getState() {
        return state;
    }

    public void initialize(AppContainer container) {
        AgentChatState current = state.getValue();
        if (current != null && current.initialized) {
            return;
        }
        state.setValue(AgentChatState.loading());
        executor.execute(() -> {
            try {
                AgentSession session = resolveSession(container);
                List<AgentMessage> messages = container.getAgentSessionHistoryUseCase.execute(session.id);
                String contextSummary = session.latestContextSummary == null || session.latestContextSummary.trim().isEmpty()
                        ? "Recent mood context is ready."
                        : session.latestContextSummary;
                state.postValue(AgentChatState.ready(messages, contextSummary, extractDiagnostic(messages)));
            } catch (Exception e) {
                state.postValue(AgentChatState.error(
                        "Unable to open chat right now. Please try again in a moment."
                ));
            }
        });
    }

    public void sendMessage(AppContainer container, String messageText) {
        String trimmed = messageText == null ? "" : messageText.trim();
        AgentChatState current = state.getValue();
        if (trimmed.isEmpty() || current == null || activeSessionId == null || current.sending) {
            return;
        }

        List<AgentMessage> optimisticMessages = new ArrayList<>(current.messages);
        optimisticMessages.add(new AgentMessage(
                "pending-user",
                activeSessionId,
                "user",
                trimmed,
                "",
                "pending",
                System.currentTimeMillis()
        ));
        optimisticMessages.add(new AgentMessage(
                "pending-assistant",
                activeSessionId,
                "assistant",
                "Thinking...",
                "",
                "thinking",
                System.currentTimeMillis()
        ));
        state.setValue(current.withSending(true, optimisticMessages, current.contextSummary, null));

        executor.execute(() -> {
            try {
                container.sendAgentMessageUseCase.execute(activeSessionId, trimmed);
                List<AgentMessage> updatedMessages = container.getAgentSessionHistoryUseCase.execute(activeSessionId);
                AgentSession latestSession = findCurrentSession(container, activeSessionId);
                String summary = latestSession != null && latestSession.latestContextSummary != null
                        && !latestSession.latestContextSummary.trim().isEmpty()
                        ? latestSession.latestContextSummary
                        : current.contextSummary;
                state.postValue(current.withSending(false, updatedMessages, summary, extractDiagnostic(updatedMessages)));
            } catch (Exception e) {
                state.postValue(current.withSending(
                        false,
                        current.messages,
                        current.contextSummary,
                        "Message failed to send. Check your chat API config or try again."
                ));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }

    private AgentSession resolveSession(AppContainer container) {
        List<AgentSession> sessions = container.agentRepository.listSessions();
        AgentSession session;
        if (sessions.isEmpty()) {
            session = container.startAgentSessionUseCase.execute("New chat");
        } else {
            session = sessions.get(0);
        }
        activeSessionId = session.id;
        return session;
    }

    private AgentSession findCurrentSession(AppContainer container, String sessionId) {
        List<AgentSession> sessions = container.agentRepository.listSessions();
        for (AgentSession session : sessions) {
            if (session.id.equals(sessionId)) {
                return session;
            }
        }
        return null;
    }

    public static class AgentChatState {
        public final boolean initialized;
        public final boolean loading;
        public final boolean sending;
        public final List<AgentMessage> messages;
        public final String contextSummary;
        public final String errorMessage;

        private AgentChatState(
                boolean initialized,
                boolean loading,
                boolean sending,
                List<AgentMessage> messages,
                String contextSummary,
                String errorMessage
        ) {
            this.initialized = initialized;
            this.loading = loading;
            this.sending = sending;
            this.messages = messages;
            this.contextSummary = contextSummary;
            this.errorMessage = errorMessage;
        }

        public static AgentChatState idle() {
            return new AgentChatState(false, false, false, new ArrayList<>(), "", null);
        }

        public static AgentChatState loading() {
            return new AgentChatState(false, true, false, new ArrayList<>(), "", null);
        }

        public static AgentChatState ready(List<AgentMessage> messages, String contextSummary, String errorMessage) {
            return new AgentChatState(true, false, false, messages, contextSummary, errorMessage);
        }

        public static AgentChatState error(String message) {
            return new AgentChatState(false, false, false, new ArrayList<>(), "", message);
        }

        public AgentChatState withSending(
                boolean sending,
                List<AgentMessage> messages,
                String contextSummary,
                String errorMessage
        ) {
            return new AgentChatState(true, false, sending, messages, contextSummary, errorMessage);
        }
    }

    private String extractDiagnostic(List<AgentMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            AgentMessage message = messages.get(i);
            if (!"assistant".equals(message.role)) {
                continue;
            }
            if (message.status == null || !message.status.contains("fallback")) {
                return null;
            }
            String diagnostic = extractJsonField(message.promptContextJson, "\"diagnostic\"");
            if (diagnostic != null && !diagnostic.trim().isEmpty()) {
                return diagnostic;
            }
            return "Qwen switched to fallback mode. Check your DashScope API settings and network connection.";
        }
        return null;
    }

    private String extractJsonField(String json, String key) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) {
            return null;
        }
        int colonIndex = json.indexOf(':', keyIndex + key.length());
        int startQuote = json.indexOf('"', colonIndex + 1);
        if (colonIndex < 0 || startQuote < 0) {
            return null;
        }
        StringBuilder value = new StringBuilder();
        boolean escaping = false;
        for (int i = startQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaping) {
                switch (c) {
                    case 'n':
                        value.append('\n');
                        break;
                    case 'r':
                        value.append('\r');
                        break;
                    case 't':
                        value.append('\t');
                        break;
                    default:
                        value.append(c);
                        break;
                }
                escaping = false;
                continue;
            }
            if (c == '\\') {
                escaping = true;
                continue;
            }
            if (c == '"') {
                return value.toString();
            }
            value.append(c);
        }
        return null;
    }
}
