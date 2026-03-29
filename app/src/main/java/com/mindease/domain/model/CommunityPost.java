package com.mindease.domain.model;

public class CommunityPost {
    public final String id;
    public final String content;
    public final String emotionTag;
    public final long createdAt;
    public final int supportCount;
    public final int likeCount;

    public CommunityPost(
            String id,
            String content,
            String emotionTag,
            long createdAt,
            int supportCount,
            int likeCount
    ) {
        this.id = id;
        this.content = content;
        this.emotionTag = emotionTag;
        this.createdAt = createdAt;
        this.supportCount = supportCount;
        this.likeCount = likeCount;
    }
}
