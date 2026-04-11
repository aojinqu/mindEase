package com.mindease.domain.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ContentModerationService {
    private static final Set<String> BANNED_WORDS = new HashSet<>(Arrays.asList(
            "hate",
            "kill",
            "idiot",
            "stupid"
    ));

    public String sanitize(String rawContent) {
        if (rawContent == null) {
            return "";
        }
        String normalized = rawContent.trim().replaceAll("\\s+", " ");
        if (normalized.isEmpty()) {
            return "";
        }
        String sanitized = normalized;
        for (String banned : BANNED_WORDS) {
            sanitized = replaceIgnoreCase(sanitized, banned, repeat('*', banned.length()));
        }
        return sanitized;
    }

    public boolean shouldFlag(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        String lower = content.toLowerCase(Locale.US);
        for (String banned : BANNED_WORDS) {
            if (lower.contains(banned)) {
                return true;
            }
        }
        return false;
    }

    private String replaceIgnoreCase(String source, String target, String replacement) {
        String lowerSource = source.toLowerCase(Locale.US);
        String lowerTarget = target.toLowerCase(Locale.US);
        int start = 0;
        StringBuilder sb = new StringBuilder();
        while (true) {
            int index = lowerSource.indexOf(lowerTarget, start);
            if (index < 0) {
                sb.append(source.substring(start));
                break;
            }
            sb.append(source, start, index).append(replacement);
            start = index + target.length();
        }
        return sb.toString();
    }

    private String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
