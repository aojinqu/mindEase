package com.mindease.app;

import android.app.Application;

import com.mindease.common.session.SessionManager;

public class MindEaseApp extends Application {
    private AppContainer appContainer;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);
        appContainer = new AppContainer(this, sessionManager);
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
