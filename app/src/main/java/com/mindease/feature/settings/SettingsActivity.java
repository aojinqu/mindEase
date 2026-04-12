package com.mindease.feature.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mindease.R;
import com.mindease.app.MindEaseApp;
import com.mindease.common.ui.WindowInsetsHelper;
import com.mindease.common.session.SessionManager;
import com.mindease.feature.auth.AuthActivity;

public class SettingsActivity extends AppCompatActivity {
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowInsetsHelper.enableEdgeToEdge(this);
        setContentView(R.layout.activity_settings);
        WindowInsetsHelper.applyTopAndBottomPadding(findViewById(R.id.root_settings));
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        SessionManager sessionManager = ((MindEaseApp) getApplication()).getSessionManager();
        SettingsViewModel.SettingsState state = viewModel.load(sessionManager);
        com.google.android.material.switchmaterial.SwitchMaterial privacySwitch = findViewById(R.id.switch_privacy_mode);
        privacySwitch.setChecked(state.privacyMode);

        privacySwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.setPrivacyMode(sessionManager, isChecked)
        );

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            viewModel.logout(sessionManager);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
