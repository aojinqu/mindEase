package com.mindease.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mindease.data.repository.CommunityRepositoryImpl;
import com.mindease.domain.model.CommunityPost;

import org.junit.Test;

import java.util.List;

public class CommunityRepositoryImplTest {

    @Test
    public void createAndList_shouldReturnNewestFirst() throws InterruptedException {
        CommunityRepositoryImpl repository = new CommunityRepositoryImpl();
        repository.createPost("first post", "Stress");
        Thread.sleep(2L);
        repository.createPost("second post", "Sleep");

        List<CommunityPost> posts = repository.listPosts();
        assertEquals(2, posts.size());
        assertEquals("second post", posts.get(0).content);
        assertEquals("first post", posts.get(1).content);
    }

    @Test
    public void filterByTag_shouldReturnMatchedPostsOnly() {
        CommunityRepositoryImpl repository = new CommunityRepositoryImpl();
        repository.createPost("stress one", "Stress");
        repository.createPost("sleep one", "Sleep");
        repository.createPost("stress two", "Stress");

        List<CommunityPost> stressPosts = repository.listPostsByTag("Stress");
        assertEquals(2, stressPosts.size());
        assertTrue(stressPosts.stream().allMatch(post -> "Stress".equals(post.emotionTag)));

        List<CommunityPost> allPosts = repository.listPostsByTag("All");
        assertEquals(3, allPosts.size());
    }
}
