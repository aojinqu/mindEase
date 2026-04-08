package com.mindease.feature.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mindease.common.session.SessionManager;

public class AuthViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loginMode = new MutableLiveData<>(true);
    private final MutableLiveData<String> message = new MutableLiveData<>("");

    public LiveData<Boolean> getLoginMode() {
        return loginMode;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void setLoginMode(boolean isLoginMode) {
        loginMode.setValue(isLoginMode);
    }

    public boolean submit(String email, String password, String confirmPassword, SessionManager sessionManager) {
        if (!isValidEmail(email)) {
            message.setValue("Please input a valid email.");
            return false;
        }
        if (password == null || password.length() < 6) {
            message.setValue("Password must be at least 6 characters.");
            return false;
        }

        boolean isLoginMode = Boolean.TRUE.equals(loginMode.getValue());
        if (!isLoginMode) {
            if (confirmPassword == null || !confirmPassword.equals(password)) {
                message.setValue("Passwords do not match.");
                return false;
            }
            sessionManager.registerAndLogin(email, password);
            message.setValue("Registration successful.");
            return true;
        }

        sessionManager.login(email, password);
        message.setValue("Login successful.");
        return true;
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }
}
