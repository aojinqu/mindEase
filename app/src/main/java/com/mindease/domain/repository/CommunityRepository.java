package com.mindease.domain.repository;

import com.mindease.domain.model.CommunityPost;
import com.mindease.domain.model.CommunityComment;

import java.util.List;

public interface CommunityRepository {
    void createPost(String content, String emotionTag);

    List<CommunityPost> listPosts();

    List<CommunityPost> listPostsByTag(String emotionTag);

    CommunityPost getPostById(String postId);

    boolean likePost(String postId);

    boolean hasLikedPost(String postId);

    CommunityComment addComment(String postId, String content);

    CommunityComment replyToComment(String postId, String parentCommentId, String content);

    List<CommunityComment> listComments(String postId);

    boolean likeComment(String postId, String commentId);

    boolean hasLikedComment(String postId, String commentId);

    boolean deleteComment(String postId, String commentId);
}
