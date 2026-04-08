package com.mindease.data.repository;

import com.mindease.common.session.SessionManager;
import com.mindease.domain.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {
    private final SessionManager sessionManager;

    public AuthRepositoryImpl() {
        this(null);
    }

    public AuthRepositoryImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean isLoggedIn() {
        return sessionManager != null && sessionManager.isLoggedIn();
    }
}
