package com.mindease.domain.repository;

import com.mindease.common.result.DataCallback;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.model.CommunityPost;

import java.util.List;

public interface CommunityRepository {
    void seedDemoPostsIfEmpty();

    void createPost(String content, String emotionTag, DataCallback<CommunityPost> callback);

    void listPosts(DataCallback<List<CommunityPost>> callback);

    void listPostsByTag(String emotionTag, DataCallback<List<CommunityPost>> callback);

    void getPostById(String postId, DataCallback<CommunityPost> callback);

    void deletePost(String postId, DataCallback<Boolean> callback);

    void togglePostLike(String postId, DataCallback<Boolean> callback);

    void hasLikedPost(String postId, DataCallback<Boolean> callback);

    void addComment(String postId, String content, DataCallback<CommunityComment> callback);

    void replyToComment(String postId, String parentCommentId, String content, DataCallback<CommunityComment> callback);

    void listComments(String postId, DataCallback<List<CommunityComment>> callback);

    void toggleCommentLike(String postId, String commentId, DataCallback<Boolean> callback);

    void hasLikedComment(String postId, String commentId, DataCallback<Boolean> callback);

    void deleteComment(String postId, String commentId, DataCallback<Boolean> callback);
}
