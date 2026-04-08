package com.mindease.feature.calendar;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.MoodRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarViewModel extends ViewModel {
    private final SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public Map<String, List<MoodRecord>> loadByDate(AppContainer container, int days) {
        List<MoodRecord> records = container.getRecentMoodRecordsUseCase.execute(days);
        Map<String, List<MoodRecord>> byDate = new LinkedHashMap<>();
        for (MoodRecord record : records) {
            String dateKey = keyFormat.format(record.createdAt);
            List<MoodRecord> dateRecords = byDate.get(dateKey);
            if (dateRecords == null) {
                dateRecords = new ArrayList<>();
                byDate.put(dateKey, dateRecords);
            }
            dateRecords.add(record);
        }
        return byDate;
    }
}
