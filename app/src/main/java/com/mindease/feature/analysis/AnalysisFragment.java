package com.mindease.feature.analysis;

import android.os.Bundle;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.AnalysisReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalysisFragment extends Fragment {
    private int currentDays = 7;
    private TextView countsTextView;
    private TextView tagsTextView;
    private TextView summaryTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        countsTextView = view.findViewById(R.id.tv_analysis_counts);
        tagsTextView = view.findViewById(R.id.tv_analysis_tags);
        summaryTextView = view.findViewById(R.id.tv_analysis_summary);

        Chip chip7 = view.findViewById(R.id.chip_period_7);
        Chip chip30 = view.findViewById(R.id.chip_period_30);
        chip7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentDays = 7;
                bindReport();
            }
        });
        chip30.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentDays = 30;
                bindReport();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindReport();
    }

    private void bindReport() {
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        AnalysisReport report = container.generateMoodAnalysisUseCase.execute(currentDays);
        countsTextView.setText(
                "Positive " + report.positiveCount
                        + " · Neutral " + report.neutralCount
                        + " · Negative " + report.negativeCount
        );
        tagsTextView.setText(topTagsWithCount(report.tagFrequency));
        summaryTextView.setText(report.summaryText);
    }

    private String topTagsWithCount(Map<String, Integer> tagFrequency) {
        if (tagFrequency.isEmpty()) {
            return "No tags found in current period";
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(tagFrequency.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        StringBuilder builder = new StringBuilder();
        int limit = Math.min(3, entries.size());
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            Map.Entry<String, Integer> entry = entries.get(i);
            builder.append(entry.getKey()).append(" (").append(entry.getValue()).append(")");
        }
        return builder.toString();
    }
}
