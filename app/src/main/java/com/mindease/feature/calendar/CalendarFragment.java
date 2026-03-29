package com.mindease.feature.calendar;

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
import com.mindease.domain.model.MoodRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    private TextView selectedTextView;
    private TextView recentListTextView;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        selectedTextView = view.findViewById(R.id.tv_calendar_selected);
        recentListTextView = view.findViewById(R.id.tv_calendar_recent_list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        List<MoodRecord> records = container.getRecentMoodRecordsUseCase.execute(30);
        if (records.isEmpty()) {
            selectedTextView.setText("No mood records yet");
            recentListTextView.setText("Record your first mood entry to populate calendar details.");
            return;
        }

        MoodRecord latest = records.get(0);
        selectedTextView.setText(
                dateFormat.format(latest.createdAt)
                        + " · mood: " + latest.moodType
                        + " · intensity: " + latest.moodIntensity
        );

        StringBuilder builder = new StringBuilder("Recent records:\n");
        int limit = Math.min(6, records.size());
        for (int i = 0; i < limit; i++) {
            MoodRecord record = records.get(i);
            builder.append("- ")
                    .append(dateFormat.format(record.createdAt))
                    .append(" · ")
                    .append(record.moodType)
                    .append(" · tags: ")
                    .append(joinTags(record.tags))
                    .append("\n");
        }
        recentListTextView.setText(builder.toString().trim());
    }

    private String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "none";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) {
                builder.append("/");
            }
            builder.append(tags.get(i));
        }
        return builder.toString();
    }
}
