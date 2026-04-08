package com.mindease.feature.mood;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;

import java.util.List;
import java.util.UUID;

public class MoodEditorViewModel extends ViewModel {

    public List<MoodRecord> loadRecent(AppContainer container) {
        return container.getRecentMoodRecordsUseCase.execute(30);
    }

    public MoodRecord create(String mood, int intensity, String diaryText, List<String> tags) {
        return new MoodRecord(
                UUID.randomUUID().toString(),
                mood,
                intensity,
                diaryText,
                tags,
                System.currentTimeMillis()
        );
    }

    public void createRecord(AppContainer container, MoodRecord record) {
        container.createMoodRecordUseCase.execute(record);
        refreshSuggestion(container);
    }

    public void updateRecord(AppContainer container, MoodRecord record) {
        container.moodRepository.update(record);
        refreshSuggestion(container);
    }

    public void deleteRecord(AppContainer container, String recordId) {
        container.moodRepository.delete(recordId);
        refreshSuggestion(container);
    }

    private void refreshSuggestion(AppContainer container) {
        AnalysisReport report = container.generateMoodAnalysisUseCase.execute(7);
        container.generateSuggestionUseCase.execute(report);
    }
}
