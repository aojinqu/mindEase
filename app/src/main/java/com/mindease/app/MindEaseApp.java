package com.mindease.app;

import android.app.Application;

public class MindEaseApp extends Application {
    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer();
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
