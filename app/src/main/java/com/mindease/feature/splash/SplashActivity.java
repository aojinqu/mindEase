package com.mindease.feature.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mindease.R;
import com.mindease.app.MindEaseApp;
import com.mindease.feature.auth.AuthActivity;
import com.mindease.feature.main.MainActivity;
import com.mindease.feature.onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {
    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        SplashViewModel.Destination destination = viewModel.resolveDestination(
                ((MindEaseApp) getApplication()).getSessionManager()
        );
        new Handler(Looper.getMainLooper()).postDelayed(() -> openDestination(destination), 450L);
    }

    private void openDestination(SplashViewModel.Destination destination) {
        Intent intent;
        if (destination == SplashViewModel.Destination.ONBOARDING) {
            intent = new Intent(this, OnboardingActivity.class);
        } else if (destination == SplashViewModel.Destination.AUTH) {
            intent = new Intent(this, AuthActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
