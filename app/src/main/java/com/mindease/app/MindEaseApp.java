package com.mindease.app;

import android.app.Application;

import com.mindease.common.session.SessionManager;

public class MindEaseApp extends Application {
    private AppContainer appContainer;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer();
        sessionManager = new SessionManager(this);
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
