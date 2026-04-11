package com.mindease.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mindease.data.repository.CommunityRepositoryImpl;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.model.CommunityPost;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.AnonymousIdentityService;
import com.mindease.domain.service.ContentModerationService;
import com.mindease.domain.service.TimeProvider;

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
        assertTrue(posts.get(0).anonymousName != null && !posts.get(0).anonymousName.isEmpty());
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

    @Test
    public void createPost_withBannedWords_shouldBeSanitized() {
        CommunityRepositoryImpl repository = new CommunityRepositoryImpl();
        repository.createPost("I hate this exam", "Stress");

        List<CommunityPost> posts = repository.listPosts();
        assertEquals(1, posts.size());
        assertTrue(posts.get(0).content.contains("****"));
    }

    @Test
    public void likeCommentReply_shouldUpdatePostAndCommentThread() {
        CommunityRepositoryImpl repository = new CommunityRepositoryImpl();
        repository.createPost("Need support", "Stress");
        CommunityPost post = repository.listPosts().get(0);

        boolean liked = repository.likePost(post.id);
        assertTrue(liked);
        CommunityPost likedPost = repository.getPostById(post.id);
        assertEquals(1, likedPost.likeCount);

        CommunityComment comment = repository.addComment(post.id, "You are not alone.");
        assertTrue(comment != null);
        CommunityComment reply = repository.replyToComment(post.id, comment.id, "Thank you.");
        assertTrue(reply != null);

        CommunityPost withComments = repository.getPostById(post.id);
        assertEquals(2, withComments.commentCount);

        List<CommunityComment> comments = repository.listComments(post.id);
        assertEquals(2, comments.size());
        assertEquals(comment.id, comments.get(0).id);
        assertEquals(reply.id, comments.get(1).id);
        assertEquals(0, comments.get(0).likeCount);
        assertTrue(repository.likeComment(post.id, comment.id));
        assertEquals(1, repository.listComments(post.id).get(0).likeCount);
    }

    @Test
    public void deleteReply_shouldRequireAuthor() {
        MutableUserRepository users = new MutableUserRepository("u_a");
        CommunityRepositoryImpl repository = new CommunityRepositoryImpl(
                users,
                new AnonymousIdentityService(),
                new ContentModerationService(),
                new FixedTimeProvider(1000L)
        );
        repository.createPost("Need support", "Stress");
        CommunityPost post = repository.listPosts().get(0);

        CommunityComment comment = repository.addComment(post.id, "Root comment");
        CommunityComment reply = repository.replyToComment(post.id, comment.id, "Reply from A");
        assertTrue(reply != null);

        users.current = "u_b";
        assertTrue(!repository.deleteComment(post.id, reply.id));

        users.current = "u_a";
        assertTrue(repository.deleteComment(post.id, reply.id));
        assertEquals(1, repository.getPostById(post.id).commentCount);
    }

    private static class MutableUserRepository implements UserRepository {
        private String current;

        private MutableUserRepository(String initial) {
            this.current = initial;
        }

        @Override
        public String currentUserId() {
            return current;
        }
    }

    private static class FixedTimeProvider implements TimeProvider {
        private long now;

        private FixedTimeProvider(long now) {
            this.now = now;
        }

        @Override
        public long nowMillis() {
            return now++;
        }
    }
}
