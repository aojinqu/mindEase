package com.mindease.feature.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.feature.agent.AgentChatActivity;
import com.mindease.feature.mood.MoodEditorActivity;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView moodEmojiTextView;
    private TextView moodTitleTextView;
    private TextView moodBodyTextView;
    private View moodHistoryCard;
    private View[] historyRows;
    private TextView[] historyEmojiViews;
    private TextView[] historyTitleViews;
    private TextView[] historyMetaViews;
    private View progressFillView;
    private View progressRestView;
    private HomeViewModel viewModel;
    private List<MoodRecord> recentRecords;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        moodEmojiTextView = view.findViewById(R.id.tv_home_mood_emoji);
        moodTitleTextView = view.findViewById(R.id.tv_home_mood_title);
        moodBodyTextView = view.findViewById(R.id.tv_home_mood_body);
        moodHistoryCard = view.findViewById(R.id.card_mood_history);
        historyRows = new View[]{
                view.findViewById(R.id.row_history_1),
                view.findViewById(R.id.row_history_2),
                view.findViewById(R.id.row_history_3)
        };
        historyEmojiViews = new TextView[]{
                view.findViewById(R.id.tv_history_emoji_1),
                view.findViewById(R.id.tv_history_emoji_2),
                view.findViewById(R.id.tv_history_emoji_3)
        };
        historyTitleViews = new TextView[]{
                view.findViewById(R.id.tv_history_title_1),
                view.findViewById(R.id.tv_history_title_2),
                view.findViewById(R.id.tv_history_title_3)
        };
        historyMetaViews = new TextView[]{
                view.findViewById(R.id.tv_history_meta_1),
                view.findViewById(R.id.tv_history_meta_2),
                view.findViewById(R.id.tv_history_meta_3)
        };
        progressFillView = view.findViewById(R.id.view_home_progress_fill);
        progressRestView = view.findViewById(R.id.view_home_progress_rest);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View quickCheckinButton = view.findViewById(R.id.btn_quick_checkin);
        quickCheckinButton.setOnClickListener(v -> startActivity(new Intent(requireContext(), MoodEditorActivity.class)));

        View openAgentButton = view.findViewById(R.id.btn_open_agent);
        View openAgentCard = view.findViewById(R.id.card_agent_entry);
        View.OnClickListener openAgentListener = v -> startActivity(new Intent(requireContext(), AgentChatActivity.class));
        openAgentButton.setOnClickListener(openAgentListener);
        openAgentCard.setOnClickListener(openAgentListener);

        for (int i = 0; i < historyRows.length; i++) {
            final int index = i;
            historyRows[i].setOnClickListener(v -> openHistoryRecord(index));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        HomeViewModel.HomeState state = viewModel.load(container);
        recentRecords = state.recentRecords;

        moodEmojiTextView.setText("\u2728");
        moodTitleTextView.setText(displayMood(state.latestRecord != null ? state.latestRecord.moodType : moodLabel(state.report)));
        moodBodyTextView.setText(moodSupportLine(state.report, state.latestRecord));
        bindProgress(state.report);
        bindHistory(state.recentRecords);
    }

    private void openHistoryRecord(int index) {
        if (recentRecords == null || index < 0 || index >= recentRecords.size()) {
            return;
        }
        Intent intent = new Intent(requireContext(), MoodEditorActivity.class);
        intent.putExtra(MoodEditorActivity.EXTRA_RECORD_ID, recentRecords.get(index).id);
        startActivity(intent);
    }

    private void bindProgress(AnalysisReport report) {
        int fillWeight = progressWeight(report);
        int emptyWeight = Math.max(8, 100 - fillWeight);

        LinearLayout.LayoutParams fillParams = (LinearLayout.LayoutParams) progressFillView.getLayoutParams();
        fillParams.weight = fillWeight;
        progressFillView.setLayoutParams(fillParams);

        LinearLayout.LayoutParams restParams = (LinearLayout.LayoutParams) progressRestView.getLayoutParams();
        restParams.weight = emptyWeight;
        progressRestView.setLayoutParams(restParams);
    }

    private int progressWeight(AnalysisReport report) {
        if (report.totalCount <= 0) {
            return 42;
        }
        double positiveAndNeutral = (double) (report.positiveCount + report.neutralCount) / report.totalCount;
        return Math.max(24, Math.min(92, (int) Math.round(positiveAndNeutral * 100d)));
    }

    private String moodLabel(AnalysisReport report) {
        if (report.totalCount <= 0) {
            return "Calm";
        }
        if (report.negativeCount > report.positiveCount && report.negativeCount >= report.neutralCount) {
            return "Tender";
        }
        if (report.neutralCount > report.positiveCount) {
            return "Steady";
        }
        return "Calm";
    }

    private String moodSupportLine(AnalysisReport report, MoodRecord latestRecord) {
        if (latestRecord != null) {
            return latestRecord.diaryText == null || latestRecord.diaryText.trim().isEmpty()
                    ? "Have a nice day."
                    : latestRecord.diaryText.trim();
        }
        if (report.totalCount <= 0) {
            return "Take a breath. You're doing okay.";
        }
        if (report.negativeCount > report.positiveCount && report.negativeCount >= report.neutralCount) {
            return "Go gently today. A softer pace can still be progress.";
        }
        if (report.neutralCount > report.positiveCount) {
            return "You're finding balance. Keep moving at your own rhythm.";
        }
        return "Take a breath. You're doing okay.";
    }

    private void bindHistory(List<MoodRecord> records) {
        if (records == null || records.isEmpty()) {
            moodHistoryCard.setVisibility(View.GONE);
            return;
        }
        moodHistoryCard.setVisibility(View.VISIBLE);
        int limit = Math.min(3, records.size());
        for (int i = 0; i < historyRows.length; i++) {
            if (i < limit) {
                MoodRecord record = records.get(i);
                historyRows[i].setVisibility(View.VISIBLE);
                historyTitleViews[i].setText(displayMood(record.moodType));
                historyEmojiViews[i].setText(moodEmoji(record));
                historyMetaViews[i].setText(historyMeta(record));
            } else {
                historyRows[i].setVisibility(View.GONE);
            }
        }
    }

    private String historyMeta(MoodRecord record) {
        String time = DateUtils.getRelativeTimeSpanString(
                record.createdAt,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString();
        String tags = record.tags == null || record.tags.isEmpty() ? "no tags" : joinTags(record.tags);
        return time + " • " + tags;
    }

    private String joinTags(List<String> tags) {
        int limit = Math.min(2, tags.size());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(tags.get(i));
        }
        return builder.toString();
    }

    private String displayMood(String mood) {
        if (mood == null || mood.trim().isEmpty()) {
            return "Calm";
        }
        String trimmed = mood.trim();
        return trimmed.substring(0, 1).toUpperCase(Locale.US) + trimmed.substring(1);
    }

    private String moodEmoji(MoodRecord record) {
        if (record == null || record.moodType == null) {
            return "\uD83D\uDE0C";
        }
        String mood = record.moodType.trim().toLowerCase(Locale.US);
        if (mood.contains("happy")) {
            return "\uD83D\uDE0A";
        }
        if (mood.contains("anxious") || mood.contains("stress") || mood.contains("nervous")) {
            return "\uD83D\uDE30";
        }
        if (mood.contains("sad") || mood.contains("down")) {
            return "\uD83D\uDE22";
        }
        if (mood.contains("angry") || mood.contains("mad")) {
            return "\uD83D\uDE20";
        }
        if (mood.contains("calm") || mood.contains("steady") || mood.contains("peace")) {
            return "\uD83D\uDE0C";
        }
        return "\u263A\uFE0F";
    }
}
