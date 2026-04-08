package com.mindease.feature.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.MoodRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {
    private TextView selectedTextView;
    private TextView recentListTextView;
    private ListView datesListView;
    private CalendarViewModel viewModel;
    private final List<String> dateKeys = new ArrayList<>();
    private final Map<String, List<MoodRecord>> recordsByDate = new LinkedHashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        selectedTextView = view.findViewById(R.id.tv_calendar_selected);
        recentListTextView = view.findViewById(R.id.tv_calendar_recent_list);
        datesListView = view.findViewById(R.id.lv_calendar_dates);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        datesListView.setOnItemClickListener((parent, v, position, id) -> {
            if (position < 0 || position >= dateKeys.size()) {
                return;
            }
            bindSelectedDate(dateKeys.get(position));
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        recordsByDate.clear();
        recordsByDate.putAll(viewModel.loadByDate(container, 30));

        dateKeys.clear();
        dateKeys.addAll(recordsByDate.keySet());
        datesListView.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_activated_1,
                dateKeys
        ));

        if (dateKeys.isEmpty()) {
            selectedTextView.setText("No mood records yet");
            recentListTextView.setText("Record your first mood entry to populate calendar details.");
            return;
        }
        bindSelectedDate(dateKeys.get(0));
    }

    private void bindSelectedDate(String dateKey) {
        List<MoodRecord> records = recordsByDate.get(dateKey);
        if (records == null || records.isEmpty()) {
            selectedTextView.setText(dateKey);
            recentListTextView.setText("No records for this date.");
            return;
        }

        int avgIntensity = 0;
        for (MoodRecord record : records) {
            avgIntensity += record.moodIntensity;
        }
        avgIntensity = avgIntensity / records.size();

        selectedTextView.setText(
                dateKey + " | records: " + records.size() + " | avg intensity: " + avgIntensity
        );

        StringBuilder builder = new StringBuilder();
        for (MoodRecord record : records) {
            builder.append("- ")
                    .append(record.moodType)
                    .append(" | intensity ")
                    .append(record.moodIntensity)
                    .append(" | tags ")
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
