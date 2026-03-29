package com.mindease.app;

import com.mindease.data.repository.AnalysisRepositoryImpl;
import com.mindease.data.repository.CommunityRepositoryImpl;
import com.mindease.data.repository.MoodRepositoryImpl;
import com.mindease.data.repository.SuggestionRepositoryImpl;
import com.mindease.domain.repository.AnalysisRepository;
import com.mindease.domain.repository.CommunityRepository;
import com.mindease.domain.repository.MoodRepository;
import com.mindease.domain.repository.SuggestionRepository;
import com.mindease.domain.service.RuleBasedSentimentAnalyzer;
import com.mindease.domain.service.SuggestionEngine;
import com.mindease.domain.usecase.CreateMoodRecordUseCase;
import com.mindease.domain.usecase.GenerateMoodAnalysisUseCase;
import com.mindease.domain.usecase.GenerateSuggestionUseCase;
import com.mindease.domain.usecase.GetRecentMoodRecordsUseCase;

public class AppContainer {
    public final MoodRepository moodRepository;
    public final AnalysisRepository analysisRepository;
    public final SuggestionRepository suggestionRepository;
    public final CommunityRepository communityRepository;

    public final CreateMoodRecordUseCase createMoodRecordUseCase;
    public final GetRecentMoodRecordsUseCase getRecentMoodRecordsUseCase;
    public final GenerateMoodAnalysisUseCase generateMoodAnalysisUseCase;
    public final GenerateSuggestionUseCase generateSuggestionUseCase;

    public AppContainer() {
        moodRepository = new MoodRepositoryImpl();
        analysisRepository = new AnalysisRepositoryImpl(moodRepository, new RuleBasedSentimentAnalyzer());
        suggestionRepository = new SuggestionRepositoryImpl();
        communityRepository = new CommunityRepositoryImpl();

        createMoodRecordUseCase = new CreateMoodRecordUseCase(moodRepository);
        getRecentMoodRecordsUseCase = new GetRecentMoodRecordsUseCase(moodRepository);
        generateMoodAnalysisUseCase = new GenerateMoodAnalysisUseCase(analysisRepository);
        generateSuggestionUseCase = new GenerateSuggestionUseCase(suggestionRepository, new SuggestionEngine());

        communityRepository.createPost("Finals week is hard. Any quick stress reset tips?", "Stress");
        communityRepository.createPost("Could not sleep last night, trying breathing exercises.", "Sleep");
    }
}
