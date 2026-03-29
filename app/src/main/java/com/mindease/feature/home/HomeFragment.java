package com.mindease.feature.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.Suggestion;
import com.mindease.feature.mood.MoodEditorActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private TextView summaryTextView;
    private TextView topTagsTextView;
    private TextView suggestionTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        summaryTextView = view.findViewById(R.id.tv_home_summary);
        topTagsTextView = view.findViewById(R.id.tv_home_top_tags);
        suggestionTextView = view.findViewById(R.id.tv_home_suggestion);
        View quickCheckinButton = view.findViewById(R.id.btn_quick_checkin);
        quickCheckinButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MoodEditorActivity.class);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        AnalysisReport report = container.generateMoodAnalysisUseCase.execute(7);
        Suggestion suggestion = container.generateSuggestionUseCase.execute(report);

        summaryTextView.setText(report.summaryText);
        topTagsTextView.setText("Top tags: " + topTags(report.tagFrequency));
        suggestionTextView.setText(suggestion.text);
    }

    private String topTags(Map<String, Integer> tagFrequency) {
        if (tagFrequency.isEmpty()) {
            return "No tags yet";
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(tagFrequency.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        StringBuilder builder = new StringBuilder();
        int limit = Math.min(3, entries.size());
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                builder.append(" · ");
            }
            builder.append(entries.get(i).getKey());
        }
        return builder.toString();
    }
}
