package com.mindease.data.repository;

import com.mindease.common.session.SessionManager;
import com.mindease.domain.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class UserRepositoryImpl implements UserRepository {
    private final SessionManager sessionManager;

    public UserRepositoryImpl() {
        this(null);
    }

    public UserRepositoryImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String currentUserId() {
        if (sessionManager == null || !sessionManager.isLoggedIn()) {
            return "guest";
        }
        String email = sessionManager.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return "guest";
        }
        return "u_" + shortHash(email.trim().toLowerCase(Locale.US));
    }

    private String shortHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format(Locale.US, "%02x", b));
            }
            return sb.substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(value.hashCode());
        }
    }
}
