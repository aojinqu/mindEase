package com.mindease.feature.analysis;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.service.RuleBasedSentimentAnalyzer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AnalysisFragment extends Fragment {
    private static final int COLOR_POSITIVE = 0xFF74C7AF;
    private static final int COLOR_NEUTRAL = 0xFFF4C781;
    private static final int COLOR_NEGATIVE = 0xFFF08D9E;

    private int currentDays = 7;
    private TextView positiveTextView;
    private TextView neutralTextView;
    private TextView negativeTextView;
    private TextView legendPositiveTextView;
    private TextView legendNeutralTextView;
    private TextView legendNegativeTextView;
    private TextView latestMoodTextView;
    private TextView latestTimeTextView;
    private TextView latestEmojiTextView;
    private TextView primaryDistributionTextView;
    private TextView secondaryDistributionTextView;
    private TextView distributionLegendPositiveTextView;
    private TextView distributionLegendNeutralTextView;
    private TextView distributionLegendNegativeTextView;
    private ChipGroup tagsChipGroup;
    private MoodTrendChartView trendChartView;
    private MoodDistributionView distributionView;
    private AnalysisViewModel viewModel;
    private final RuleBasedSentimentAnalyzer analyzer = new RuleBasedSentimentAnalyzer();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        positiveTextView = view.findViewById(R.id.tv_analysis_positive);
        neutralTextView = view.findViewById(R.id.tv_analysis_neutral);
        negativeTextView = view.findViewById(R.id.tv_analysis_negative);
        legendPositiveTextView = view.findViewById(R.id.tv_analysis_legend_positive);
        legendNeutralTextView = view.findViewById(R.id.tv_analysis_legend_neutral);
        legendNegativeTextView = view.findViewById(R.id.tv_analysis_legend_negative);
        latestMoodTextView = view.findViewById(R.id.tv_analysis_latest_mood);
        latestTimeTextView = view.findViewById(R.id.tv_analysis_latest_time);
        latestEmojiTextView = view.findViewById(R.id.tv_analysis_latest_emoji);
        primaryDistributionTextView = view.findViewById(R.id.tv_analysis_distribution_primary);
        secondaryDistributionTextView = view.findViewById(R.id.tv_analysis_distribution_secondary);
        distributionLegendPositiveTextView = view.findViewById(R.id.tv_distribution_legend_positive);
        distributionLegendNeutralTextView = view.findViewById(R.id.tv_distribution_legend_neutral);
        distributionLegendNegativeTextView = view.findViewById(R.id.tv_distribution_legend_negative);
        tagsChipGroup = view.findViewById(R.id.chip_group_analysis_tags);
        trendChartView = view.findViewById(R.id.chart_mood_trend);
        distributionView = view.findViewById(R.id.chart_sentiment_distribution);
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

        positiveTextView.setText(buildLegendText("Positive", report.positiveCount, COLOR_POSITIVE));
        neutralTextView.setText(buildLegendText("Neutral", report.neutralCount, COLOR_NEUTRAL));
        negativeTextView.setText(buildLegendText("Negative", report.negativeCount, COLOR_NEGATIVE));
        legendPositiveTextView.setText(buildLegendText("Positive", -1, COLOR_POSITIVE));
        legendNeutralTextView.setText(buildLegendText("Neutral", -1, COLOR_NEUTRAL));
        legendNegativeTextView.setText(buildLegendText("Negative", -1, COLOR_NEGATIVE));

        bindTagChips(report.tagFrequency);
        bindLatestRecord(records);
        bindDistribution(records, report);
        bindTrend(records);
    }

    private CharSequence buildLegendText(String label, int count, int color) {
        String text = count >= 0 ? "\u25CF " + label + " " + count : "\u25CF " + label;
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private void bindTagChips(Map<String, Integer> tagFrequency) {
        tagsChipGroup.removeAllViews();
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(tagFrequency.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        if (entries.isEmpty()) {
            tagsChipGroup.addView(createTagChip("No tags yet"));
            return;
        }

        int limit = Math.min(4, entries.size());
        for (int i = 0; i < limit; i++) {
            tagsChipGroup.addView(createTagChip(entries.get(i).getKey()));
        }
    }

    private Chip createTagChip(String text) {
        Context context = requireContext();
        Chip chip = new Chip(context);
        chip.setText(text);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipBackgroundColorResource(R.color.chip_filter_background);
        chip.setChipStrokeColorResource(R.color.chip_filter_stroke);
        chip.setTextColor(context.getResources().getColorStateList(R.color.chip_filter_text, context.getTheme()));
        chip.setChipStrokeWidth(dpToPx(1));
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        chip.setEnsureMinTouchTargetSize(false);
        return chip;
    }

    private void bindLatestRecord(List<MoodRecord> records) {
        MoodRecord latest = null;
        for (MoodRecord record : records) {
            if (latest == null || record.createdAt > latest.createdAt) {
                latest = record;
            }
        }
        latestMoodTextView.setText(latest == null ? "Calm" : displayMood(latest.moodType));
        latestEmojiTextView.setText(latest == null ? "\u2601\uFE0F" : moodEmoji(latest.moodType));
        latestTimeTextView.setText(latest == null ? "No entries yet" : formatRelativeTime(latest.createdAt));
    }

    private void bindDistribution(List<MoodRecord> records, AnalysisReport report) {
        Map<String, Integer> moodFrequency = new HashMap<>();
        for (MoodRecord record : records) {
            String mood = displayMood(record.moodType);
            moodFrequency.put(mood, moodFrequency.getOrDefault(mood, 0) + 1);
        }

        List<Map.Entry<String, Integer>> moods = new ArrayList<>(moodFrequency.entrySet());
        moods.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        primaryDistributionTextView.setText(buildDistributionLine("Top mood", moods, 0));
        secondaryDistributionTextView.setText(buildDistributionLine("Second mood", moods, 1));

        int total = Math.max(1, report.totalCount);
        distributionLegendPositiveTextView.setText(buildDistributionLegendLine("Positive", report.positiveCount, total, COLOR_POSITIVE));
        distributionLegendNeutralTextView.setText(buildDistributionLegendLine("Neutral", report.neutralCount, total, COLOR_NEUTRAL));
        distributionLegendNegativeTextView.setText(buildDistributionLegendLine("Negative", report.negativeCount, total, COLOR_NEGATIVE));
        distributionView.setData(report.positiveCount, report.neutralCount, report.negativeCount);
    }

    private String buildDistributionLine(String prefix, List<Map.Entry<String, Integer>> moods, int index) {
        if (moods.size() <= index) {
            return prefix + ": not enough data yet";
        }
        Map.Entry<String, Integer> entry = moods.get(index);
        int total = 0;
        for (Map.Entry<String, Integer> mood : moods) {
            total += mood.getValue();
        }
        int percent = total == 0 ? 0 : Math.round(entry.getValue() * 100f / total);
        return prefix + ": " + entry.getKey() + " " + percent + "%";
    }

    private CharSequence buildDistributionLegendLine(String label, int count, int total, int color) {
        int percent = total == 0 ? 0 : Math.round(count * 100f / total);
        String text = "\u25CF " + label + ": " + percent + "% (" + count + ")";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private void bindTrend(List<MoodRecord> records) {
        TrendData trendData = currentDays <= 7
                ? buildSevenDayTrend(records)
                : buildThirtyDayTrend(records);
        trendChartView.setData(trendData.points, trendData.labels, trendData.details);
    }

    private TrendData buildSevenDayTrend(List<MoodRecord> records) {
        Calendar start = startOfToday();
        start.add(Calendar.DAY_OF_YEAR, -6);
        long dayStart = start.getTimeInMillis();
        int bucketCount = 7;
        Bucket[] buckets = createBuckets(bucketCount);
        List<String> labels = new ArrayList<>();

        Calendar labelCursor = (Calendar) start.clone();
        for (int i = 0; i < bucketCount; i++) {
            labels.add(new SimpleDateFormat("EEE", Locale.US).format(labelCursor.getTime()));
            labelCursor.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (MoodRecord record : records) {
            long delta = record.createdAt - dayStart;
            int index = (int) TimeUnit.MILLISECONDS.toDays(delta);
            if (index < 0 || index >= bucketCount) {
                continue;
            }
            addRecordToBucket(buckets[index], record);
        }
        return buildTrendData(buckets, labels);
    }

    private TrendData buildThirtyDayTrend(List<MoodRecord> records) {
        Calendar start = startOfToday();
        start.add(Calendar.DAY_OF_YEAR, -29);
        long windowStart = start.getTimeInMillis();
        int bucketCount = 30;
        Bucket[] buckets = createBuckets(bucketCount);
        List<String> labels = new ArrayList<>();

        Calendar labelCursor = (Calendar) start.clone();
        for (int i = 0; i < bucketCount; i++) {
            if (i % 5 == 0 || i == bucketCount - 1) {
                labels.add(new SimpleDateFormat("M/d", Locale.US).format(labelCursor.getTime()));
            } else {
                labels.add("");
            }
            labelCursor.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (MoodRecord record : records) {
            long delta = record.createdAt - windowStart;
            int index = (int) TimeUnit.MILLISECONDS.toDays(delta);
            if (index < 0 || index >= bucketCount) {
                continue;
            }
            addRecordToBucket(buckets[index], record);
        }
        return buildTrendData(buckets, labels);
    }

    private Bucket[] createBuckets(int count) {
        Bucket[] buckets = new Bucket[count];
        for (int i = 0; i < count; i++) {
            buckets[i] = new Bucket();
        }
        return buckets;
    }

    private void addRecordToBucket(Bucket bucket, MoodRecord record) {
        String label = analyzer.analyzeLabel(record.moodType, record.diaryText, record.moodIntensity);
        if ("positive".equals(label)) {
            bucket.positiveCount++;
        } else if ("negative".equals(label)) {
            bucket.negativeCount++;
        } else {
            bucket.neutralCount++;
        }
        bucket.totalCount++;
    }

    private TrendData buildTrendData(Bucket[] buckets, List<String> labels) {
        List<Integer> points = new ArrayList<>();
        List<String> details = new ArrayList<>();
        for (Bucket bucket : buckets) {
            points.add(bucketValue(bucket));
            details.add(bucketDetail(bucket));
        }
        return new TrendData(points, labels, details);
    }

    private int bucketValue(Bucket bucket) {
        if (bucket.totalCount == 0) {
            return 0;
        }
        if (bucket.negativeCount > 0 && bucket.negativeCount >= bucket.positiveCount) {
            return -1;
        }
        if (bucket.positiveCount > 0 && bucket.positiveCount > bucket.negativeCount) {
            return 1;
        }
        return 0;
    }

    private String bucketDetail(Bucket bucket) {
        if (bucket.totalCount == 0) {
            return "No records";
        }
        return bucket.totalCount + " entries"
                + "\nPositive " + bucket.positiveCount
                + "  Neutral " + bucket.neutralCount
                + "  Negative " + bucket.negativeCount;
    }

    private Calendar startOfToday() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private String formatRelativeTime(long createdAt) {
        long diff = Math.max(0L, System.currentTimeMillis() - createdAt);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        if (minutes < 1) {
            return "Just now";
        }
        if (minutes < 60) {
            return minutes + " minutes ago";
        }
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        if (hours < 24) {
            return hours + " hours ago";
        }
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        return days + " days ago";
    }

    private String displayMood(String rawMood) {
        if (rawMood == null || rawMood.trim().isEmpty()) {
            return "Calm";
        }
        String normalized = rawMood.trim().toLowerCase(Locale.US);
        if ("happy".equals(normalized)) {
            return "Happy";
        }
        if ("anxious".equals(normalized)) {
            return "Anxious";
        }
        if ("sad".equals(normalized)) {
            return "Sad";
        }
        if ("calm".equals(normalized)) {
            return "Calm";
        }
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String moodEmoji(String rawMood) {
        if (rawMood == null) {
            return "\u2601\uFE0F";
        }
        String normalized = rawMood.trim().toLowerCase(Locale.US);
        if ("happy".equals(normalized)) {
            return "\uD83D\uDE0A";
        }
        if ("anxious".equals(normalized)) {
            return "\uD83D\uDE1F";
        }
        if ("sad".equals(normalized)) {
            return "\uD83D\uDE14";
        }
        if ("calm".equals(normalized)) {
            return "\u2601\uFE0F";
        }
        return "\u2601\uFE0F";
    }

    private float dpToPx(int value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                requireContext().getResources().getDisplayMetrics()
        );
    }

    private static class TrendData {
        final List<Integer> points;
        final List<String> labels;
        final List<String> details;

        TrendData(List<Integer> points, List<String> labels, List<String> details) {
            this.points = points;
            this.labels = labels;
            this.details = details;
        }
    }

    private static class Bucket {
        int positiveCount;
        int neutralCount;
        int negativeCount;
        int totalCount;
    }
}
