package com.mindease.feature.settings;

import androidx.lifecycle.ViewModel;

import com.mindease.common.session.SessionManager;

public class SettingsViewModel extends ViewModel {

    public SettingsState load(SessionManager sessionManager) {
        return new SettingsState(
                sessionManager.isPrivacyModeEnabled(),
                sessionManager.isDailyReminderEnabled()
        );
    }

    public void setPrivacyMode(SessionManager sessionManager, boolean enabled) {
        sessionManager.setPrivacyModeEnabled(enabled);
    }

    public void setDailyReminder(SessionManager sessionManager, boolean enabled) {
        sessionManager.setDailyReminderEnabled(enabled);
    }

    public void logout(SessionManager sessionManager) {
        sessionManager.logout();
    }

    public static class SettingsState {
        public final boolean privacyMode;
        public final boolean dailyReminder;

        public SettingsState(boolean privacyMode, boolean dailyReminder) {
            this.privacyMode = privacyMode;
            this.dailyReminder = dailyReminder;
        }
    }
}
