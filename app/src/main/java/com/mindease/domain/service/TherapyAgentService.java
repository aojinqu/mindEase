package com.mindease.domain.service;

import com.mindease.BuildConfig;
import com.mindease.domain.model.AgentMessage;
import com.mindease.domain.model.AgentPromptContext;
import com.mindease.domain.model.AgentReply;
import com.mindease.domain.model.RiskAssessment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TherapyAgentService {
    private static final String PROVIDER_NAME = "qwen-compatible";

    public AgentReply generateReply(
            String userMessage,
            List<AgentMessage> recentMessages,
            AgentPromptContext context,
            RiskAssessment riskAssessment
    ) {
        try {
            String remoteReply = generateByRemote(userMessage, recentMessages, context, riskAssessment);
            if (remoteReply == null || remoteReply.trim().isEmpty()) {
                throw new IllegalStateException("Empty remote reply");
            }
            return new AgentReply(
                    appendRiskGuidance(remoteReply.trim(), riskAssessment),
                    resolvedModelName(),
                    PROVIDER_NAME,
                    riskAssessment.highRisk ? "success_with_risk_notice" : "success",
                    null
            );
        } catch (Exception e) {
            return new AgentReply(
                    buildFallbackReply(context, riskAssessment),
                    resolvedModelName(),
                    PROVIDER_NAME,
                    riskAssessment.highRisk ? "fallback_with_risk_notice" : "fallback",
                    buildDiagnosticMessage(e)
            );
        }
    }

    protected String generateByRemote(
            String userMessage,
            List<AgentMessage> recentMessages,
            AgentPromptContext context,
            RiskAssessment riskAssessment
    ) throws IOException {
        if (isBlank(BuildConfig.CHAT_API_BASE_URL) || isBlank(BuildConfig.CHAT_API_KEY)) {
            throw new IllegalStateException("Chat API config is missing.");
        }

        URL url = new URL(normalizeUrl(BuildConfig.CHAT_API_BASE_URL));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Authorization", "Bearer " + BuildConfig.CHAT_API_KEY);

        String payload = buildRequestBody(userMessage, recentMessages, context, riskAssessment);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int statusCode = connection.getResponseCode();
        InputStream stream = statusCode >= 200 && statusCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream();
        String responseBody = readAll(stream);
        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("Remote agent request failed: " + statusCode + " " + responseBody);
        }

        String content = extractReplyText(responseBody);
        if (isBlank(content)) {
            throw new IOException("Reply text missing from response. Response body: " + responseBody);
        }
        return content;
    }

    private String buildRequestBody(
            String userMessage,
            List<AgentMessage> recentMessages,
            AgentPromptContext context,
            RiskAssessment riskAssessment
    ) {
        StringBuilder messagesJson = new StringBuilder();
        messagesJson.append("[");
        messagesJson.append(messageJson("system", buildSystemPrompt(riskAssessment)));
        messagesJson.append(",");
        messagesJson.append(messageJson("system", "User context: " + context.summaryText));

        int start = Math.max(0, recentMessages.size() - 6);
        for (int i = start; i < recentMessages.size(); i++) {
            AgentMessage message = recentMessages.get(i);
            messagesJson.append(",");
            messagesJson.append(messageJson(message.role, message.text));
        }
        messagesJson.append(",");
        messagesJson.append(messageJson("user", userMessage));
        messagesJson.append("]");

        return "{"
                + "\"model\":\"" + escapeJson(resolvedModelName()) + "\","
                + "\"temperature\":0.7,"
                + "\"stream\":false,"
                + "\"messages\":" + messagesJson
                + "}";
    }

    private String buildSystemPrompt(RiskAssessment riskAssessment) {
        String basePrompt = "You are MindEase, a warm and supportive emotional companion . "
                + "Do not claim to diagnose, treat, or provide medical advice. "
                + "Keep replies practical, short, gentle, and action-oriented. "
                + "When appropriate, acknowledge recent mood patterns and suggest one small next step.";
        if (!riskAssessment.highRisk) {
            return basePrompt;
        }
        return basePrompt + " The user may be at elevated risk, so respond with empathy, encourage immediate human support, "
                + "and clearly suggest crisis or emergency resources.Remember to use emojis.";
    }

    private String buildFallbackReply(AgentPromptContext context, RiskAssessment riskAssessment) {
        StringBuilder builder = new StringBuilder();
        builder.append("I am here with you. From your recent context, ");
        builder.append(context.summaryText);
        builder.append(".\nLet's focus on one small next step: pause for a minute, loosen your shoulders, and name what feels heaviest right now.");
        if (riskAssessment.highRisk) {
            builder.append(" ").append(riskAssessment.guidanceText);
        }
        return builder.toString();
    }

    private String appendRiskGuidance(String reply, RiskAssessment riskAssessment) {
        if (!riskAssessment.highRisk || isBlank(riskAssessment.guidanceText)) {
            return reply;
        }
        return reply + "\n\n" + riskAssessment.guidanceText;
    }

    private String messageJson(String role, String content) {
        return "{"
                + "\"role\":\"" + escapeJson(role) + "\","
                + "\"content\":\"" + escapeJson(content) + "\""
                + "}";
    }

    private String normalizeUrl(String baseUrl) {
        String trimmed = baseUrl.trim();
        if (trimmed.endsWith("/chat/completions")) {
            return trimmed;
        }
        if (trimmed.endsWith("/")) {
            return trimmed + "chat/completions";
        }
        return trimmed + "/chat/completions";
    }

    private String extractReplyText(String json) {
        String fromChoicesMessage = extractChoicesMessageContent(json);
        if (!isBlank(fromChoicesMessage)) {
            return fromChoicesMessage;
        }
        String fromReply = extractJsonString(json, "\"reply\"");
        if (!isBlank(fromReply)) {
            return fromReply;
        }
        int messageIndex = json.indexOf("\"message\"");
        if (messageIndex >= 0) {
            String fromMessage = extractJsonString(json.substring(messageIndex), "\"content\"");
            if (!isBlank(fromMessage)) {
                return fromMessage;
            }
        }
        return extractJsonString(json, "\"content\"");
    }

    private String extractChoicesMessageContent(String json) {
        int choicesIndex = json.indexOf("\"choices\"");
        if (choicesIndex < 0) {
            return null;
        }
        int messageIndex = json.indexOf("\"message\"", choicesIndex);
        if (messageIndex < 0) {
            return null;
        }
        return extractJsonString(json.substring(messageIndex), "\"content\"");
    }

    private String extractJsonString(String json, String key) {
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
                    case 't':
                        value.append('\t');
                        break;
                    case 'r':
                        value.append('\r');
                        break;
                    case '\\':
                    case '"':
                    case '/':
                        value.append(c);
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

    private String readAll(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private String resolvedModelName() {
        if (isBlank(BuildConfig.CHAT_MODEL)) {
            return "qwen-plus";
        }
        return BuildConfig.CHAT_MODEL;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String buildDiagnosticMessage(Exception exception) {
        String detail = exception == null || exception.getMessage() == null
                ? ""
                : exception.getMessage().trim();
        String lower = detail.toLowerCase();

        if (lower.contains("config is missing")) {
            return "Qwen fallback is active because API config is missing. Check mindease.chat.baseUrl, mindease.chat.apiKey, and mindease.chat.model.";
        }
        if (lower.contains("401") || lower.contains("unauthorized")) {
            return "Qwen authentication failed. Check whether your DashScope API key is valid and still active.";
        }
        if (lower.contains("403")) {
            return "Qwen request was rejected with 403. Check model permissions and account authorization in DashScope.";
        }
        if (lower.contains("429")) {
            return "Qwen hit a quota or rate limit. Check DashScope quota and billing, or wait a moment and retry.";
        }
        if (lower.contains("timed out") || lower.contains("timeout")) {
            return "Qwen request timed out. Check your network or try a faster model such as qwen-turbo for debugging.";
        }
        if (lower.contains("reply text missing")) {
            return "Qwen returned data, but the app could not find choices[0].message.content. Check whether the proxy response is fully OpenAI compatible.";
        }
        if (lower.contains("remote agent request failed")) {
            return "Qwen request failed. Detail: " + detail;
        }
        if (detail.isEmpty()) {
            return "Qwen request failed and the app switched to fallback mode. Check your network and DashScope settings.";
        }
        return "Qwen request failed and the app switched to fallback mode. Detail: " + detail;
    }
}
