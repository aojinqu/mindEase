package com.mindease.domain.model;

public class CommunityPost {
    public final String id;
    public final String anonymousName;
    public final String content;
    public final String emotionTag;
    public final long createdAt;
    public final int supportCount;
    public final int likeCount;
    public final int commentCount;

    public CommunityPost(
            String id,
            String anonymousName,
            String content,
            String emotionTag,
            long createdAt,
            int supportCount,
            int likeCount,
            int commentCount
    ) {
        this.id = id;
        this.anonymousName = anonymousName;
        this.content = content;
        this.emotionTag = emotionTag;
        this.createdAt = createdAt;
        this.supportCount = supportCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
