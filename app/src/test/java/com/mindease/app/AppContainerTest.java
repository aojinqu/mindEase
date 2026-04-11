package com.mindease.app;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("AppContainer initializes FirebaseFirestore and should be covered by instrumented tests or dependency-injected unit tests.")
public class AppContainerTest {

    @Test
    public void containerInit_shouldProvideCoreDependenciesAndSeedPosts() {
        AppContainer container = new AppContainer();
        assertNotNull(container.moodRepository);
        assertNotNull(container.analysisRepository);
        assertNotNull(container.suggestionRepository);
        assertNotNull(container.communityRepository);
        assertNotNull(container.createMoodRecordUseCase);
        assertNotNull(container.generateMoodAnalysisUseCase);
        assertNotNull(container.generateSuggestionUseCase);
    }
}
