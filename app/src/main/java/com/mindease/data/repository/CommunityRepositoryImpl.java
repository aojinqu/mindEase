package com.mindease.data.repository;

import com.mindease.domain.model.CommunityPost;
import com.mindease.domain.repository.CommunityRepository;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.AnonymousIdentityService;
import com.mindease.domain.service.ContentModerationService;
import com.mindease.domain.service.SystemTimeProvider;
import com.mindease.domain.service.TimeProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CommunityRepositoryImpl implements CommunityRepository {
    private final List<CommunityPost> posts = new ArrayList<>();
    private final UserRepository userRepository;
    private final AnonymousIdentityService anonymousIdentityService;
    private final ContentModerationService moderationService;
    private final TimeProvider timeProvider;

    public CommunityRepositoryImpl() {
        this(
                new UserRepositoryImpl(),
                new AnonymousIdentityService(),
                new ContentModerationService(),
                new SystemTimeProvider()
        );
    }

    public CommunityRepositoryImpl(
            UserRepository userRepository,
            AnonymousIdentityService anonymousIdentityService,
            ContentModerationService moderationService,
            TimeProvider timeProvider
    ) {
        this.userRepository = userRepository;
        this.anonymousIdentityService = anonymousIdentityService;
        this.moderationService = moderationService;
        this.timeProvider = timeProvider;
    }

    @Override
    public void createPost(String content, String emotionTag) {
        String cleaned = moderationService.sanitize(content);
        if (cleaned.isEmpty()) {
            return;
        }
        String userId = userRepository.currentUserId();
        CommunityPost post = new CommunityPost(
                UUID.randomUUID().toString(),
                anonymousIdentityService.displayNameForUser(userId),
                cleaned,
                emotionTag,
                timeProvider.nowMillis(),
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

    @Override
    public CommunityPost getPostById(String postId) {
        if (postId == null) {
            return null;
        }
        for (CommunityPost post : posts) {
            if (postId.equals(post.id)) {
                return post;
            }
        }
        return null;
    }
}
