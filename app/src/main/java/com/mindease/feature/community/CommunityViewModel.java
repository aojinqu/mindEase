package com.mindease.feature.community;

import androidx.lifecycle.ViewModel;

import com.mindease.app.AppContainer;
import com.mindease.common.result.DataCallback;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.model.CommunityPost;

import java.util.List;

public class CommunityViewModel extends ViewModel {

    public void loadPosts(AppContainer container, String filter, DataCallback<List<CommunityPost>> callback) {
        container.communityRepository.listPostsByTag(filter, callback);
    }

    public void createPost(AppContainer container, String content, String tag, DataCallback<CommunityPost> callback) {
        container.communityRepository.createPost(content, tag, callback);
    }

    public void getPost(AppContainer container, String postId, DataCallback<CommunityPost> callback) {
        container.communityRepository.getPostById(postId, callback);
    }

    public void likePost(AppContainer container, String postId, DataCallback<Boolean> callback) {
        container.communityRepository.likePost(postId, callback);
    }

    public void hasLikedPost(AppContainer container, String postId, DataCallback<Boolean> callback) {
        container.communityRepository.hasLikedPost(postId, callback);
    }

    public void loadComments(AppContainer container, String postId, DataCallback<List<CommunityComment>> callback) {
        container.communityRepository.listComments(postId, callback);
    }

    public void addComment(AppContainer container, String postId, String content, DataCallback<CommunityComment> callback) {
        container.communityRepository.addComment(postId, content, callback);
    }

    public void replyToComment(AppContainer container, String postId, String parentCommentId, String content, DataCallback<CommunityComment> callback) {
        container.communityRepository.replyToComment(postId, parentCommentId, content, callback);
    }

    public void likeComment(AppContainer container, String postId, String commentId, DataCallback<Boolean> callback) {
        container.communityRepository.likeComment(postId, commentId, callback);
    }

    public void hasLikedComment(AppContainer container, String postId, String commentId, DataCallback<Boolean> callback) {
        container.communityRepository.hasLikedComment(postId, commentId, callback);
    }

    public void deleteComment(AppContainer container, String postId, String commentId, DataCallback<Boolean> callback) {
        container.communityRepository.deleteComment(postId, commentId, callback);
    }
}
