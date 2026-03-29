package com.mindease.domain.repository;

import com.mindease.domain.model.CommunityPost;

import java.util.List;

public interface CommunityRepository {
    void createPost(String content, String emotionTag);

    List<CommunityPost> listPosts();

    List<CommunityPost> listPostsByTag(String emotionTag);
}
