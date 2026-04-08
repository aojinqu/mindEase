package com.mindease.app;

import android.content.Context;

import androidx.room.Room;

import com.mindease.common.session.SessionManager;
import com.mindease.data.local.db.MindEaseDatabase;
import com.mindease.data.repository.AnalysisRepositoryImpl;
import com.mindease.data.repository.CommunityRepositoryImpl;
import com.mindease.data.repository.AuthRepositoryImpl;
import com.mindease.data.repository.MoodRepositoryImpl;
import com.mindease.data.repository.RoomMoodRepository;
import com.mindease.data.repository.SuggestionRepositoryImpl;
import com.mindease.data.repository.UserRepositoryImpl;
import com.mindease.domain.repository.AnalysisRepository;
import com.mindease.domain.repository.AuthRepository;
import com.mindease.domain.repository.CommunityRepository;
import com.mindease.domain.repository.MoodRepository;
import com.mindease.domain.repository.SuggestionRepository;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.AnonymousIdentityService;
import com.mindease.domain.service.ContentModerationService;
import com.mindease.domain.service.RuleBasedSentimentAnalyzer;
import com.mindease.domain.service.SuggestionEngine;
import com.mindease.domain.service.SystemTimeProvider;
import com.mindease.domain.usecase.CreateMoodRecordUseCase;
import com.mindease.domain.usecase.GenerateMoodAnalysisUseCase;
import com.mindease.domain.usecase.GenerateSuggestionUseCase;
import com.mindease.domain.usecase.GetRecentMoodRecordsUseCase;

public class AppContainer {
    public final AuthRepository authRepository;
    public final UserRepository userRepository;
    public final MoodRepository moodRepository;
    public final AnalysisRepository analysisRepository;
    public final SuggestionRepository suggestionRepository;
    public final CommunityRepository communityRepository;

    public final CreateMoodRecordUseCase createMoodRecordUseCase;
    public final GetRecentMoodRecordsUseCase getRecentMoodRecordsUseCase;
    public final GenerateMoodAnalysisUseCase generateMoodAnalysisUseCase;
    public final GenerateSuggestionUseCase generateSuggestionUseCase;

    public AppContainer() {
        authRepository = new AuthRepositoryImpl();
        userRepository = new UserRepositoryImpl();
        moodRepository = new MoodRepositoryImpl();
        analysisRepository = new AnalysisRepositoryImpl(moodRepository, new RuleBasedSentimentAnalyzer());
        suggestionRepository = new SuggestionRepositoryImpl(userRepository);
        communityRepository = new CommunityRepositoryImpl(
                userRepository,
                new AnonymousIdentityService(),
                new ContentModerationService(),
                new SystemTimeProvider()
        );

        createMoodRecordUseCase = new CreateMoodRecordUseCase(moodRepository);
        getRecentMoodRecordsUseCase = new GetRecentMoodRecordsUseCase(moodRepository);
        generateMoodAnalysisUseCase = new GenerateMoodAnalysisUseCase(analysisRepository);
        generateSuggestionUseCase = new GenerateSuggestionUseCase(suggestionRepository, new SuggestionEngine());

        communityRepository.createPost("Finals week is hard. Any quick stress reset tips?", "Stress");
        communityRepository.createPost("Could not sleep last night, trying breathing exercises.", "Sleep");
    }

    public AppContainer(Context context, SessionManager sessionManager) {
        authRepository = new AuthRepositoryImpl(sessionManager);
        userRepository = new UserRepositoryImpl(sessionManager);

        MindEaseDatabase database = Room.databaseBuilder(
                context.getApplicationContext(),
                MindEaseDatabase.class,
                "mindease.db"
        ).allowMainThreadQueries().build();

        moodRepository = new RoomMoodRepository(
                database.moodRecordDao(),
                database.moodTagDao(),
                database.moodRecordTagDao(),
                userRepository
        );
        analysisRepository = new AnalysisRepositoryImpl(moodRepository, new RuleBasedSentimentAnalyzer());
        suggestionRepository = new SuggestionRepositoryImpl(userRepository);
        communityRepository = new CommunityRepositoryImpl(
                userRepository,
                new AnonymousIdentityService(),
                new ContentModerationService(),
                new SystemTimeProvider()
        );

        createMoodRecordUseCase = new CreateMoodRecordUseCase(moodRepository);
        getRecentMoodRecordsUseCase = new GetRecentMoodRecordsUseCase(moodRepository);
        generateMoodAnalysisUseCase = new GenerateMoodAnalysisUseCase(analysisRepository);
        generateSuggestionUseCase = new GenerateSuggestionUseCase(suggestionRepository, new SuggestionEngine());

        if (communityRepository.listPosts().isEmpty()) {
            communityRepository.createPost("Finals week is hard. Any quick stress reset tips?", "Stress");
            communityRepository.createPost("Could not sleep last night, trying breathing exercises.", "Sleep");
        }
    }
}
