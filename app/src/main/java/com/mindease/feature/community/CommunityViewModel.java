package com.mindease.feature.community;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.model.CommunityPost;

import java.util.List;

public class CommunityViewModel extends ViewModel {

    public List<CommunityPost> loadPosts(AppContainer container, String filter) {
        return container.communityRepository.listPostsByTag(filter);
    }

    public void createPost(AppContainer container, String content, String tag) {
        container.communityRepository.createPost(content, tag);
    }

    public boolean likePost(AppContainer container, String postId) {
        return container.communityRepository.likePost(postId);
    }

    public boolean hasLikedPost(AppContainer container, String postId) {
        return container.communityRepository.hasLikedPost(postId);
    }

    public List<CommunityComment> loadComments(AppContainer container, String postId) {
        return container.communityRepository.listComments(postId);
    }

    public CommunityComment addComment(AppContainer container, String postId, String content) {
        return container.communityRepository.addComment(postId, content);
    }

    public CommunityComment replyToComment(AppContainer container, String postId, String parentCommentId, String content) {
        return container.communityRepository.replyToComment(postId, parentCommentId, content);
    }

    public boolean likeComment(AppContainer container, String postId, String commentId) {
        return container.communityRepository.likeComment(postId, commentId);
    }

    public boolean hasLikedComment(AppContainer container, String postId, String commentId) {
        return container.communityRepository.hasLikedComment(postId, commentId);
    }

    public boolean deleteComment(AppContainer container, String postId, String commentId) {
        return container.communityRepository.deleteComment(postId, commentId);
    }
}
