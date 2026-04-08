package com.mindease.feature.analysis;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;

import java.util.ArrayList;
import java.util.List;

public class AnalysisViewModel extends ViewModel {

    public AnalysisReport loadReport(AppContainer container, int days) {
        return container.generateMoodAnalysisUseCase.execute(days);
    }

    public List<MoodRecord> loadRecords(AppContainer container, int days) {
        return new ArrayList<>(container.getRecentMoodRecordsUseCase.execute(days));
    }
}
