package com.mindease.data.repository;

import com.mindease.domain.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {
    @Override
    public boolean isLoggedIn() {
        return false;
    }
}
