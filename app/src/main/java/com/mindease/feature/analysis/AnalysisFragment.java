package com.mindease.feature.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.chip.Chip;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalysisFragment extends Fragment {
    private int currentDays = 7;
    private TextView countsTextView;
    private TextView tagsTextView;
    private TextView summaryTextView;
    private LineChart trendChart;
    private BarChart distributionChart;
    private AnalysisViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        countsTextView = view.findViewById(R.id.tv_analysis_counts);
        tagsTextView = view.findViewById(R.id.tv_analysis_tags);
        summaryTextView = view.findViewById(R.id.tv_analysis_summary);
        trendChart = view.findViewById(R.id.chart_mood_trend);
        distributionChart = view.findViewById(R.id.chart_sentiment_distribution);
        viewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);

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
        AnalysisReport report = viewModel.loadReport(container, currentDays);
        List<MoodRecord> records = viewModel.loadRecords(container, currentDays);

        countsTextView.setText(
                "Positive " + report.positiveCount
                        + " | Neutral " + report.neutralCount
                        + " | Negative " + report.negativeCount
        );
        tagsTextView.setText(topTagsWithCount(report.tagFrequency));
        summaryTextView.setText(report.summaryText);

        renderTrendChart(records);
        renderDistributionChart(report);
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

    private void renderTrendChart(List<MoodRecord> records) {
        if (records.isEmpty()) {
            trendChart.clear();
            trendChart.setNoDataText("No data in this period");
            trendChart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        int size = records.size();
        for (int i = 0; i < size; i++) {
            MoodRecord record = records.get(size - 1 - i);
            entries.add(new Entry(i, record.moodIntensity));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Mood Intensity");
        dataSet.setColor(0xFF4A6FA5);
        dataSet.setCircleColor(0xFF4A6FA5);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);

        trendChart.setData(new LineData(dataSet));
        trendChart.getDescription().setEnabled(false);
        trendChart.getAxisRight().setEnabled(false);
        trendChart.getXAxis().setDrawGridLines(false);
        trendChart.getAxisLeft().setAxisMinimum(1f);
        trendChart.getAxisLeft().setAxisMaximum(5f);
        trendChart.invalidate();
    }

    private void renderDistributionChart(AnalysisReport report) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, report.positiveCount));
        entries.add(new BarEntry(1f, report.neutralCount));
        entries.add(new BarEntry(2f, report.negativeCount));

        BarDataSet dataSet = new BarDataSet(entries, "Sentiment");
        dataSet.setColors(new int[]{0xFF66BB6A, 0xFFFFCA28, 0xFFEF5350});
        dataSet.setDrawValues(false);

        distributionChart.setData(new BarData(dataSet));
        distributionChart.getDescription().setEnabled(false);
        distributionChart.getAxisRight().setEnabled(false);
        distributionChart.getXAxis().setDrawGridLines(false);
        distributionChart.invalidate();
    }
}
