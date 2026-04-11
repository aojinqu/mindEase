package com.mindease.domain.model;

public class CommunityComment {
    public final String id;
    public final String postId;
    public final String parentCommentId;
    public final String authorUserId;
    public final String anonymousName;
    public final String content;
    public final long createdAt;
    public final int likeCount;

    public CommunityComment(
            String id,
            String postId,
            String parentCommentId,
            String authorUserId,
            String anonymousName,
            String content,
            long createdAt,
            int likeCount
    ) {
        this.id = id;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.authorUserId = authorUserId;
        this.anonymousName = anonymousName;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
    }

    public boolean isReply() {
        return parentCommentId != null && !parentCommentId.trim().isEmpty();
    }
}
