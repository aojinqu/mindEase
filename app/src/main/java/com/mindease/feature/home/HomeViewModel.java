package com.mindease.feature.home;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.model.Suggestion;

import java.util.List;

public class HomeViewModel extends ViewModel {

    public HomeState load(AppContainer container) {
        AnalysisReport report = container.generateMoodAnalysisUseCase.execute(7);
        Suggestion suggestion = container.generateSuggestionUseCase.execute(report);
        List<MoodRecord> recentRecords = container.getRecentMoodRecordsUseCase.execute(30);
        MoodRecord latestRecord = recentRecords.isEmpty() ? null : recentRecords.get(0);
        return new HomeState(report, suggestion.text, latestRecord, recentRecords);
    }

    public static class HomeState {
        public final AnalysisReport report;
        public final String suggestionText;
        public final MoodRecord latestRecord;
        public final List<MoodRecord> recentRecords;

        public HomeState(
                AnalysisReport report,
                String suggestionText,
                MoodRecord latestRecord,
                List<MoodRecord> recentRecords
        ) {
            this.report = report;
            this.suggestionText = suggestionText;
            this.latestRecord = latestRecord;
            this.recentRecords = recentRecords;
        }
    }
}
