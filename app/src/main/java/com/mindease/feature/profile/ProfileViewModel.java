package com.mindease.feature.profile;

import androidx.lifecycle.ViewModel;

import com.mindease.common.session.SessionManager;

public class ProfileViewModel extends ViewModel {

    public ProfileState load(SessionManager sessionManager) {
        return new ProfileState(
                sessionManager.getNickname(),
                sessionManager.getAnonymousName(),
                sessionManager.getEmail()
        );
    }

    public void save(SessionManager sessionManager, String nickname, String anonymousName) {
        sessionManager.setNickname(nickname);
        sessionManager.setAnonymousName(anonymousName);
    }

    public static class ProfileState {
        public final String nickname;
        public final String anonymousName;
        public final String email;

        public ProfileState(String nickname, String anonymousName, String email) {
            this.nickname = nickname;
            this.anonymousName = anonymousName;
            this.email = email;
        }
    }
}
