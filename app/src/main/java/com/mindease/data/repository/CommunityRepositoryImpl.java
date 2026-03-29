package com.mindease.data.repository;

import com.mindease.domain.model.CommunityPost;
import com.mindease.domain.repository.CommunityRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CommunityRepositoryImpl implements CommunityRepository {
    private final List<CommunityPost> posts = new ArrayList<>();

    @Override
    public void createPost(String content, String emotionTag) {
        CommunityPost post = new CommunityPost(
                UUID.randomUUID().toString(),
                content,
                emotionTag,
                System.currentTimeMillis(),
                0,
                0
        );
        posts.add(post);
    }

    @Override
    public List<CommunityPost> listPosts() {
        List<CommunityPost> copy = new ArrayList<>(posts);
        copy.sort(Comparator.comparingLong((CommunityPost p) -> p.createdAt).reversed());
        return Collections.unmodifiableList(copy);
    }

    @Override
    public List<CommunityPost> listPostsByTag(String emotionTag) {
        if (emotionTag == null || emotionTag.trim().isEmpty() || "All".equalsIgnoreCase(emotionTag)) {
            return listPosts();
        }
        List<CommunityPost> filtered = new ArrayList<>();
        for (CommunityPost post : posts) {
            if (emotionTag.equalsIgnoreCase(post.emotionTag)) {
                filtered.add(post);
            }
        }
        filtered.sort(Comparator.comparingLong((CommunityPost p) -> p.createdAt).reversed());
        return Collections.unmodifiableList(filtered);
    }
}
