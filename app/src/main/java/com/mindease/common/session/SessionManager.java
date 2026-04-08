package com.mindease.common.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "mindease_session";
    private static final String KEY_ONBOARDING_DONE = "onboarding_done";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_ANON_NAME = "anonymous_name";
    private static final String KEY_PRIVACY_MODE = "privacy_mode";
    private static final String KEY_DAILY_REMINDER = "daily_reminder";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isOnboardingDone() {
        return preferences.getBoolean(KEY_ONBOARDING_DONE, false);
    }

    public void setOnboardingDone(boolean done) {
        preferences.edit().putBoolean(KEY_ONBOARDING_DONE, done).apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void registerAndLogin(String email, String password) {
        // Local-only auth for MVP UI flow.
        login(email, password);
    }

    public void login(String email, String password) {
        if (email == null) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email.trim());
        if (getNickname().isEmpty()) {
            String prefix = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            editor.putString(KEY_NICKNAME, prefix);
        }
        if (getAnonymousName().isEmpty()) {
            editor.putString(KEY_ANON_NAME, "QuietPanda");
        }
        editor.apply();
    }

    public void logout() {
        preferences.edit().putBoolean(KEY_LOGGED_IN, false).remove(KEY_EMAIL).apply();
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getNickname() {
        return preferences.getString(KEY_NICKNAME, "CampusFox");
    }

    public void setNickname(String nickname) {
        preferences.edit().putString(KEY_NICKNAME, safeTrim(nickname)).apply();
    }

    public String getAnonymousName() {
        return preferences.getString(KEY_ANON_NAME, "QuietPanda");
    }

    public void setAnonymousName(String name) {
        preferences.edit().putString(KEY_ANON_NAME, safeTrim(name)).apply();
    }

    public boolean isPrivacyModeEnabled() {
        return preferences.getBoolean(KEY_PRIVACY_MODE, true);
    }

    public void setPrivacyModeEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_PRIVACY_MODE, enabled).apply();
    }

    public boolean isDailyReminderEnabled() {
        return preferences.getBoolean(KEY_DAILY_REMINDER, false);
    }

    public void setDailyReminderEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_DAILY_REMINDER, enabled).apply();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
