package com.mindease.feature.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mindease.R;
import com.mindease.app.MindEaseApp;
import com.mindease.common.session.SessionManager;
import com.mindease.feature.settings.SettingsActivity;

public class ProfileFragment extends Fragment {
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        com.google.android.material.textfield.TextInputEditText nicknameEditText = view.findViewById(R.id.et_profile_nickname);
        com.google.android.material.textfield.TextInputEditText anonymousEditText = view.findViewById(R.id.et_profile_anon_name);
        TextView emailTextView = view.findViewById(R.id.tv_profile_email);

        SessionManager sessionManager = ((MindEaseApp) requireActivity().getApplication()).getSessionManager();
        ProfileViewModel.ProfileState state = viewModel.load(sessionManager);
        nicknameEditText.setText(state.nickname);
        anonymousEditText.setText(state.anonymousName);
        emailTextView.setText(state.email.isEmpty() ? "Email: not logged in" : "Email: " + state.email);

        view.findViewById(R.id.btn_profile_save).setOnClickListener(v -> {
            viewModel.save(
                    sessionManager,
                    getText(nicknameEditText),
                    getText(anonymousEditText)
            );
            Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btn_profile_open_settings).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingsActivity.class));
        });
        return view;
    }

    private String getText(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
