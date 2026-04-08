package com.mindease.feature.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mindease.R;
import com.mindease.app.MindEaseApp;
import com.mindease.common.session.SessionManager;
import com.mindease.feature.main.MainActivity;

public class AuthActivity extends AppCompatActivity {
    private AuthViewModel viewModel;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextView messageTextView;
    private TextInputLayout confirmLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private MaterialButton submitButton;
    private MaterialButton switchModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        bindViews();
        bindActions();
        renderMode(true);
    }

    private void bindViews() {
        titleTextView = findViewById(R.id.tv_auth_title);
        subtitleTextView = findViewById(R.id.tv_auth_subtitle);
        messageTextView = findViewById(R.id.tv_auth_message);
        confirmLayout = findViewById(R.id.layout_auth_confirm);
        emailEditText = findViewById(R.id.et_auth_email);
        passwordEditText = findViewById(R.id.et_auth_password);
        confirmPasswordEditText = findViewById(R.id.et_auth_confirm_password);
        submitButton = findViewById(R.id.btn_auth_submit);
        switchModeButton = findViewById(R.id.btn_auth_switch_mode);
    }

    private void bindActions() {
        switchModeButton.setOnClickListener(v -> {
            boolean currentMode = Boolean.TRUE.equals(viewModel.getLoginMode().getValue());
            boolean newMode = !currentMode;
            viewModel.setLoginMode(newMode);
            renderMode(newMode);
        });

        submitButton.setOnClickListener(v -> {
            SessionManager sessionManager = ((MindEaseApp) getApplication()).getSessionManager();
            boolean success = viewModel.submit(
                    getText(emailEditText),
                    getText(passwordEditText),
                    getText(confirmPasswordEditText),
                    sessionManager
            );
            String message = viewModel.getMessage().getValue();
            messageTextView.setText(message == null ? "" : message);
            if (success) {
                Toast.makeText(this, "Welcome to MindEase", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    private void renderMode(boolean loginMode) {
        if (loginMode) {
            titleTextView.setText("Login");
            subtitleTextView.setText("Welcome back");
            confirmLayout.setVisibility(View.GONE);
            submitButton.setText("Login");
            switchModeButton.setText("No account? Register");
        } else {
            titleTextView.setText("Register");
            subtitleTextView.setText("Create your MindEase account");
            confirmLayout.setVisibility(View.VISIBLE);
            submitButton.setText("Register");
            switchModeButton.setText("Already have an account? Login");
        }
        messageTextView.setText("");
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
