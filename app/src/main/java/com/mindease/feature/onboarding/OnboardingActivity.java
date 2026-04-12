package com.mindease.feature.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mindease.R;
import com.mindease.app.MindEaseApp;
import com.mindease.common.ui.WindowInsetsHelper;
import com.mindease.feature.auth.AuthActivity;

public class OnboardingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowInsetsHelper.enableEdgeToEdge(this);
        setContentView(R.layout.activity_onboarding);
        WindowInsetsHelper.applyTopAndBottomPadding(findViewById(R.id.root_onboarding));

        findViewById(R.id.btn_continue).setOnClickListener(v -> {
            ((MindEaseApp) getApplication()).getSessionManager().setOnboardingDone(true);
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });
    }
}
