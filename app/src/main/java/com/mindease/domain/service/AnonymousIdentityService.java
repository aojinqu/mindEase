package com.mindease.domain.service;

import java.util.Locale;

public class AnonymousIdentityService {
    private static final String[] ADJECTIVES = {
            "Quiet", "Kind", "Calm", "Gentle", "Brave", "Warm", "Bright", "Soft"
    };

    private static final String[] NOUNS = {
            "Panda", "Fox", "Whale", "Otter", "Robin", "Maple", "Cloud", "Comet"
    };

    public String displayNameForUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "QuietPanda";
        }
        int hash = Math.abs(userId.toLowerCase(Locale.US).hashCode());
        String adjective = ADJECTIVES[hash % ADJECTIVES.length];
        String noun = NOUNS[(hash / ADJECTIVES.length) % NOUNS.length];
        int suffix = 100 + (hash % 900);
        return adjective + noun + suffix;
    }
}
