package com.mindease.data.repository;

import com.mindease.domain.model.CommunityPost;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.repository.CommunityRepository;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.AnonymousIdentityService;
import com.mindease.domain.service.ContentModerationService;
import com.mindease.domain.service.SystemTimeProvider;
import com.mindease.domain.service.TimeProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommunityRepositoryImpl implements CommunityRepository {
    private final List<CommunityPost> posts = new ArrayList<>();
    private final Map<String, List<CommunityComment>> commentsByPost = new HashMap<>();
    private final Map<String, Set<String>> postLikesByUser = new HashMap<>();
    private final Map<String, Set<String>> commentLikesByUser = new HashMap<>();
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

    @Override
    public boolean likePost(String postId) {
        CommunityPost post = getPostById(postId);
        if (post == null) {
            return false;
        }
        String userId = userRepository.currentUserId();
        Set<String> likedUsers = postLikesByUser.computeIfAbsent(postId, ignored -> new HashSet<>());
        if (!likedUsers.add(userId)) {
            return false;
        }
        replacePost(post, new CommunityPost(
                post.id,
                post.anonymousName,
                post.content,
                post.emotionTag,
                post.createdAt,
                post.supportCount,
                post.likeCount + 1,
                post.commentCount
        ));
        return true;
    }

    @Override
    public boolean hasLikedPost(String postId) {
        if (postId == null) {
            return false;
        }
        Set<String> likedUsers = postLikesByUser.get(postId);
        return likedUsers != null && likedUsers.contains(userRepository.currentUserId());
    }

    @Override
    public CommunityComment addComment(String postId, String content) {
        if (getPostById(postId) == null) {
            return null;
        }
        String cleaned = moderationService.sanitize(content);
        if (cleaned.isEmpty()) {
            return null;
        }
        String userId = userRepository.currentUserId();
        CommunityComment comment = new CommunityComment(
                UUID.randomUUID().toString(),
                postId,
                null,
                userId,
                anonymousIdentityService.displayNameForUser(userId),
                cleaned,
                timeProvider.nowMillis(),
                0
        );
        commentsByPost.computeIfAbsent(postId, ignored -> new ArrayList<>()).add(comment);
        incrementCommentCount(postId);
        return comment;
    }

    @Override
    public CommunityComment replyToComment(String postId, String parentCommentId, String content) {
        if (getPostById(postId) == null || parentCommentId == null || parentCommentId.trim().isEmpty()) {
            return null;
        }
        if (findCommentById(postId, parentCommentId) == null) {
            return null;
        }
        String cleaned = moderationService.sanitize(content);
        if (cleaned.isEmpty()) {
            return null;
        }
        String userId = userRepository.currentUserId();
        CommunityComment reply = new CommunityComment(
                UUID.randomUUID().toString(),
                postId,
                parentCommentId,
                userId,
                anonymousIdentityService.displayNameForUser(userId),
                cleaned,
                timeProvider.nowMillis(),
                0
        );
        commentsByPost.computeIfAbsent(postId, ignored -> new ArrayList<>()).add(reply);
        incrementCommentCount(postId);
        return reply;
    }

    @Override
    public List<CommunityComment> listComments(String postId) {
        List<CommunityComment> raw = commentsByPost.getOrDefault(postId, Collections.emptyList());
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }

        List<CommunityComment> parents = new ArrayList<>();
        Map<String, List<CommunityComment>> children = new HashMap<>();
        for (CommunityComment comment : raw) {
            if (!comment.isReply()) {
                parents.add(comment);
            } else {
                children.computeIfAbsent(comment.parentCommentId, ignored -> new ArrayList<>()).add(comment);
            }
        }

        parents.sort(Comparator.comparingLong(c -> c.createdAt));
        for (List<CommunityComment> list : children.values()) {
            list.sort(Comparator.comparingLong(c -> c.createdAt));
        }

        List<CommunityComment> ordered = new ArrayList<>();
        for (CommunityComment parent : parents) {
            appendCommentThread(parent, children, ordered);
        }
        return Collections.unmodifiableList(ordered);
    }

    @Override
    public boolean likeComment(String postId, String commentId) {
        List<CommunityComment> comments = commentsByPost.get(postId);
        if (comments == null || comments.isEmpty()) {
            return false;
        }
        String likeKey = buildCommentLikeKey(postId, commentId);
        Set<String> likedUsers = commentLikesByUser.computeIfAbsent(likeKey, ignored -> new HashSet<>());
        if (!likedUsers.add(userRepository.currentUserId())) {
            return false;
        }
        for (int i = 0; i < comments.size(); i++) {
            CommunityComment comment = comments.get(i);
            if (!comment.id.equals(commentId)) {
                continue;
            }
            CommunityComment updated = new CommunityComment(
                    comment.id,
                    comment.postId,
                    comment.parentCommentId,
                    comment.authorUserId,
                    comment.anonymousName,
                    comment.content,
                    comment.createdAt,
                    comment.likeCount + 1
            );
            comments.set(i, updated);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasLikedComment(String postId, String commentId) {
        if (postId == null || commentId == null) {
            return false;
        }
        Set<String> likedUsers = commentLikesByUser.get(buildCommentLikeKey(postId, commentId));
        return likedUsers != null && likedUsers.contains(userRepository.currentUserId());
    }

    @Override
    public boolean deleteComment(String postId, String commentId) {
        List<CommunityComment> comments = commentsByPost.get(postId);
        if (comments == null || comments.isEmpty()) {
            return false;
        }
        CommunityComment target = findCommentById(postId, commentId);
        if (target == null) {
            return false;
        }
        if (!userRepository.currentUserId().equals(target.authorUserId)) {
            return false;
        }
        if (hasChildComments(postId, commentId)) {
            return false;
        }

        boolean removed = comments.removeIf(comment -> comment.id.equals(commentId));
        if (!removed) {
            return false;
        }
        decrementCommentCount(postId);
        return true;
    }

    private void appendCommentThread(
            CommunityComment root,
            Map<String, List<CommunityComment>> children,
            List<CommunityComment> target
    ) {
        target.add(root);
        List<CommunityComment> replies = children.get(root.id);
        if (replies == null || replies.isEmpty()) {
            return;
        }
        for (CommunityComment reply : replies) {
            appendCommentThread(reply, children, target);
        }
    }

    private CommunityComment findCommentById(String postId, String commentId) {
        List<CommunityComment> comments = commentsByPost.getOrDefault(postId, Collections.emptyList());
        for (CommunityComment comment : comments) {
            if (comment.id.equals(commentId)) {
                return comment;
            }
        }
        return null;
    }

    private boolean hasChildComments(String postId, String commentId) {
        List<CommunityComment> comments = commentsByPost.getOrDefault(postId, Collections.emptyList());
        for (CommunityComment comment : comments) {
            if (commentId.equals(comment.parentCommentId)) {
                return true;
            }
        }
        return false;
    }

    private void incrementCommentCount(String postId) {
        CommunityPost post = getPostById(postId);
        if (post == null) {
            return;
        }
        replacePost(post, new CommunityPost(
                post.id,
                post.anonymousName,
                post.content,
                post.emotionTag,
                post.createdAt,
                post.supportCount,
                post.likeCount,
                post.commentCount + 1
        ));
    }

    private void decrementCommentCount(String postId) {
        CommunityPost post = getPostById(postId);
        if (post == null || post.commentCount <= 0) {
            return;
        }
        replacePost(post, new CommunityPost(
                post.id,
                post.anonymousName,
                post.content,
                post.emotionTag,
                post.createdAt,
                post.supportCount,
                post.likeCount,
                post.commentCount - 1
        ));
    }

    private void replacePost(CommunityPost source, CommunityPost updated) {
        int index = posts.indexOf(source);
        if (index >= 0) {
            posts.set(index, updated);
        }
    }

    private String buildCommentLikeKey(String postId, String commentId) {
        return postId + ":" + commentId;
    }
}
