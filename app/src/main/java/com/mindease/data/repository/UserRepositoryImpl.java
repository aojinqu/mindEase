package com.mindease.data.repository;

import com.mindease.domain.repository.UserRepository;

public class UserRepositoryImpl implements UserRepository {
    @Override
    public String currentUserId() {
        return "guest";
    }
}
