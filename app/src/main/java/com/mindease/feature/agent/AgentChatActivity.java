package com.mindease.feature.agent;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;

public class AgentChatActivity extends AppCompatActivity {
    private AgentChatViewModel viewModel;
    private AgentMessageAdapter adapter;
    private RecyclerView recyclerView;
    private TextView contextSummaryView;
    private TextView emptyHintView;
    private TextView errorView;
    private ProgressBar progressBar;
    private TextInputEditText inputEditText;
    private MaterialButton sendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_chat);

        viewModel = new ViewModelProvider(this).get(AgentChatViewModel.class);
        adapter = new AgentMessageAdapter();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_agent_chat);
        recyclerView = findViewById(R.id.rv_agent_messages);
        contextSummaryView = findViewById(R.id.tv_agent_context_summary);
        emptyHintView = findViewById(R.id.tv_agent_empty_hint);
        errorView = findViewById(R.id.tv_agent_error);
        progressBar = findViewById(R.id.progress_agent_loading);
        inputEditText = findViewById(R.id.et_agent_input);
        sendButton = findViewById(R.id.btn_agent_send);

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> submitCurrentInput());
        inputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitCurrentInput();
                return true;
            }
            return false;
        });

        bindQuickPrompt(R.id.btn_quick_prompt_grounding, getString(R.string.agent_quick_prompt_grounding));
        bindQuickPrompt(R.id.btn_quick_prompt_stress, getString(R.string.agent_quick_prompt_stress));
        bindQuickPrompt(R.id.btn_quick_prompt_sleep, getString(R.string.agent_quick_prompt_sleep));

        AppContainer container = ((MindEaseApp) getApplication()).getAppContainer();
        viewModel.getState().observe(this, this::render);
        viewModel.initialize(container);
    }

    private void bindQuickPrompt(int buttonId, String text) {
        MaterialButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            inputEditText.setText(text);
            inputEditText.setSelection(text.length());
            submitCurrentInput();
        });
    }

    private void submitCurrentInput() {
        AppContainer container = ((MindEaseApp) getApplication()).getAppContainer();
        String message = inputEditText.getText() == null ? "" : inputEditText.getText().toString();
        if (message.trim().isEmpty()) {
            return;
        }
        inputEditText.setText("");
        viewModel.sendMessage(container, message);
    }

    private void render(AgentChatViewModel.AgentChatState state) {
        if (state == null) {
            return;
        }
        progressBar.setVisibility(state.loading ? View.VISIBLE : View.GONE);
        errorView.setVisibility(state.errorMessage == null || state.errorMessage.trim().isEmpty() ? View.GONE : View.VISIBLE);
        errorView.setText(state.errorMessage == null ? "" : state.errorMessage);
        contextSummaryView.setText(state.contextSummary);
        adapter.submitList(state.messages);
        emptyHintView.setVisibility(state.messages.isEmpty() && !state.loading ? View.VISIBLE : View.GONE);
        if (!state.messages.isEmpty()) {
            recyclerView.scrollToPosition(state.messages.size() - 1);
        }
        sendButton.setEnabled(!state.loading && !state.sending);
        inputEditText.setEnabled(!state.loading);
    }
}
