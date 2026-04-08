package com.mindease.feature.home;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.Suggestion;

public class HomeViewModel extends ViewModel {

    public HomeState load(AppContainer container) {
        AnalysisReport report = container.generateMoodAnalysisUseCase.execute(7);
        Suggestion suggestion = container.generateSuggestionUseCase.execute(report);
        return new HomeState(report, suggestion.text);
    }

    public static class HomeState {
        public final AnalysisReport report;
        public final String suggestionText;

        public HomeState(AnalysisReport report, String suggestionText) {
            this.report = report;
            this.suggestionText = suggestionText;
        }
    }
}
