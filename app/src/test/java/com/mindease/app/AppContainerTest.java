package com.mindease.app;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mindease.domain.model.CommunityPost;

import org.junit.Test;

import java.util.List;

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

        List<CommunityPost> posts = container.communityRepository.listPosts();
        assertTrue(posts.size() >= 2);
    }
}
