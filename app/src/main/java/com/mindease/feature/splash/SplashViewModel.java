package com.mindease.feature.splash;

import androidx.lifecycle.ViewModel;

import com.mindease.common.session.SessionManager;

public class SplashViewModel extends ViewModel {

    public Destination resolveDestination(SessionManager sessionManager) {
        if (!sessionManager.isOnboardingDone()) {
            return Destination.ONBOARDING;
        }
        if (!sessionManager.isLoggedIn()) {
            return Destination.AUTH;
        }
        return Destination.MAIN;
    }

    public enum Destination {
        ONBOARDING,
        AUTH,
        MAIN
    }
}
