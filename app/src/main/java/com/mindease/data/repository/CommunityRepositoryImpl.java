package com.mindease.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.mindease.common.result.DataCallback;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.model.CommunityPost;
import com.mindease.domain.repository.CommunityRepository;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.AnonymousIdentityService;
import com.mindease.domain.service.ContentModerationService;
import com.mindease.domain.service.TimeProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CommunityRepositoryImpl implements CommunityRepository {
    private static final String COLLECTION_POSTS = "community_posts";
    private static final String COLLECTION_COMMENTS = "comments";
    private static final String COLLECTION_LIKES = "likes";

    private final FirebaseFirestore firestore;
    private final UserRepository userRepository;
    private final AnonymousIdentityService anonymousIdentityService;
    private final ContentModerationService moderationService;
    private final TimeProvider timeProvider;

    public CommunityRepositoryImpl(
            FirebaseFirestore firestore,
            UserRepository userRepository,
            AnonymousIdentityService anonymousIdentityService,
            ContentModerationService moderationService,
            TimeProvider timeProvider
    ) {
        this.firestore = firestore;
        this.userRepository = userRepository;
        this.anonymousIdentityService = anonymousIdentityService;
        this.moderationService = moderationService;
        this.timeProvider = timeProvider;
    }

    @Override
    public void seedDemoPostsIfEmpty() {
        postsCollection().limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot != null && !snapshot.isEmpty()) {
                return;
            }
            createPost("Finals week is hard. Any quick stress reset tips?", "Stress", new NoOpCallback<>());
            createPost("Could not sleep last night, trying breathing exercises.", "Sleep", new NoOpCallback<>());
        });
    }

    @Override
    public void createPost(String content, String emotionTag, DataCallback<CommunityPost> callback) {
        String cleaned = moderationService.sanitize(content);
        if (cleaned.isEmpty()) {
            callback.onError("Post content cannot be empty.");
            return;
        }
        String userId = userRepository.currentUserId();
        String postId = UUID.randomUUID().toString();
        CommunityPost post = new CommunityPost(
                postId,
                userId,
                anonymousIdentityService.displayNameForUser(userId),
                cleaned,
                normalizeTag(emotionTag),
                timeProvider.nowMillis(),
                0,
                0,
                0
        );

        Map<String, Object> data = new HashMap<>();
        data.put("id", post.id);
        data.put("authorUserId", userId);
        data.put("anonymousName", post.anonymousName);
        data.put("content", post.content);
        data.put("emotionTag", post.emotionTag);
        data.put("createdAt", post.createdAt);
        data.put("supportCount", post.supportCount);
        data.put("likeCount", post.likeCount);
        data.put("commentCount", post.commentCount);

        postsCollection().document(postId).set(data)
                .addOnSuccessListener(unused -> callback.onSuccess(post))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to publish post.")));
    }

    @Override
    public void listPosts(DataCallback<List<CommunityPost>> callback) {
        postsCollection()
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(mapPosts(snapshot)))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to load posts.")));
    }

    @Override
    public void listPostsByTag(String emotionTag, DataCallback<List<CommunityPost>> callback) {
        if (emotionTag == null || emotionTag.trim().isEmpty() || "All".equalsIgnoreCase(emotionTag)) {
            listPosts(callback);
            return;
        }
        postsCollection()
                .whereEqualTo("emotionTag", normalizeTag(emotionTag))
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(mapPosts(snapshot)))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to load posts.")));
    }

    @Override
    public void getPostById(String postId, DataCallback<CommunityPost> callback) {
        if (postId == null || postId.trim().isEmpty()) {
            callback.onError("Post not found.");
            return;
        }
        postsCollection().document(postId).get()
                .addOnSuccessListener(document -> {
                    CommunityPost post = mapPost(document);
                    if (post == null) {
                        callback.onError("Post not found.");
                        return;
                    }
                    callback.onSuccess(post);
                })
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to load post.")));
    }

    @Override
    public void deletePost(String postId, DataCallback<Boolean> callback) {
        if (postId == null || postId.trim().isEmpty()) {
            callback.onError("Post not found.");
            return;
        }
        DocumentReference postRef = postsCollection().document(postId);
        postRef.get()
                .addOnSuccessListener(document -> {
                    CommunityPost post = mapPost(document);
                    if (post == null) {
                        callback.onError("Post not found.");
                        return;
                    }
                    if (!userRepository.currentUserId().equals(post.authorUserId)) {
                        callback.onError("Only the author can delete this post.");
                        return;
                    }
                    deletePostCascade(postRef, callback);
                })
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete post.")));
    }

    @Override
    public void togglePostLike(String postId, DataCallback<Boolean> callback) {
        String userId = userRepository.currentUserId();
        DocumentReference postRef = postsCollection().document(postId);
        DocumentReference likeRef = postRef.collection(COLLECTION_LIKES).document(userId);

        firestore.runTransaction((Transaction.Function<Boolean>) transaction -> {
            DocumentSnapshot postSnapshot = transaction.get(postRef);
            if (!postSnapshot.exists()) {
                throw new IllegalStateException("Post not found.");
            }
            if (transaction.get(likeRef).exists()) {
                transaction.delete(likeRef);
                transaction.update(postRef, "likeCount", FieldValue.increment(-1));
                return false;
            }
            Map<String, Object> likeData = new HashMap<>();
            likeData.put("userId", userId);
            likeData.put("createdAt", timeProvider.nowMillis());
            transaction.set(likeRef, likeData);
            transaction.update(postRef, "likeCount", FieldValue.increment(1));
            return true;
        }).addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to update post like.")));
    }

    @Override
    public void hasLikedPost(String postId, DataCallback<Boolean> callback) {
        if (postId == null || postId.trim().isEmpty()) {
            callback.onSuccess(false);
            return;
        }
        postsCollection().document(postId).collection(COLLECTION_LIKES)
                .document(userRepository.currentUserId())
                .get()
                .addOnSuccessListener(document -> callback.onSuccess(document.exists()))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to check post like.")));
    }

    @Override
    public void addComment(String postId, String content, DataCallback<CommunityComment> callback) {
        writeComment(postId, null, content, callback);
    }

    @Override
    public void replyToComment(String postId, String parentCommentId, String content, DataCallback<CommunityComment> callback) {
        if (parentCommentId == null || parentCommentId.trim().isEmpty()) {
            callback.onError("Reply target is missing.");
            return;
        }
        commentDocument(postId, parentCommentId).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        callback.onError("The comment you are replying to no longer exists.");
                        return;
                    }
                    writeComment(postId, parentCommentId, content, callback);
                })
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to publish reply.")));
    }

    @Override
    public void listComments(String postId, DataCallback<List<CommunityComment>> callback) {
        commentsCollection(postId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(orderComments(snapshot)))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to load comments.")));
    }

    @Override
    public void toggleCommentLike(String postId, String commentId, DataCallback<Boolean> callback) {
        String userId = userRepository.currentUserId();
        DocumentReference commentRef = commentDocument(postId, commentId);
        DocumentReference likeRef = commentRef.collection(COLLECTION_LIKES).document(userId);

        firestore.runTransaction((Transaction.Function<Boolean>) transaction -> {
            DocumentSnapshot commentSnapshot = transaction.get(commentRef);
            if (!commentSnapshot.exists()) {
                throw new IllegalStateException("Comment not found.");
            }
            if (transaction.get(likeRef).exists()) {
                transaction.delete(likeRef);
                transaction.update(commentRef, "likeCount", FieldValue.increment(-1));
                return false;
            }
            Map<String, Object> likeData = new HashMap<>();
            likeData.put("userId", userId);
            likeData.put("createdAt", timeProvider.nowMillis());
            transaction.set(likeRef, likeData);
            transaction.update(commentRef, "likeCount", FieldValue.increment(1));
            return true;
        }).addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to update comment like.")));
    }

    @Override
    public void hasLikedComment(String postId, String commentId, DataCallback<Boolean> callback) {
        if (postId == null || commentId == null || postId.trim().isEmpty() || commentId.trim().isEmpty()) {
            callback.onSuccess(false);
            return;
        }
        commentDocument(postId, commentId).collection(COLLECTION_LIKES)
                .document(userRepository.currentUserId())
                .get()
                .addOnSuccessListener(document -> callback.onSuccess(document.exists()))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to check comment like.")));
    }

    @Override
    public void deleteComment(String postId, String commentId, DataCallback<Boolean> callback) {
        DocumentReference commentRef = commentDocument(postId, commentId);
        commentsCollection(postId).whereEqualTo("parentCommentId", commentId).limit(1).get()
                .addOnSuccessListener(children -> {
                    if (!children.isEmpty()) {
                        callback.onError("Comments with replies cannot be deleted.");
                        return;
                    }
                    commentRef.get().addOnSuccessListener(document -> {
                        CommunityComment comment = mapComment(document);
                        if (comment == null) {
                            callback.onError("Comment not found.");
                            return;
                        }
                        if (!userRepository.currentUserId().equals(comment.authorUserId)) {
                            callback.onError("Only the author can delete this comment.");
                            return;
                        }
                        firestore.runTransaction((Transaction.Function<Boolean>) transaction -> {
                            DocumentSnapshot commentSnapshot = transaction.get(commentRef);
                            if (!commentSnapshot.exists()) {
                                throw new IllegalStateException("Comment not found.");
                            }
                            transaction.delete(commentRef);
                            transaction.update(postsCollection().document(postId), "commentCount", FieldValue.increment(-1));
                            return true;
                        }).addOnSuccessListener(callback::onSuccess)
                                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete comment.")));
                    }).addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete comment.")));
                })
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete comment.")));
    }

    private void writeComment(
            String postId,
            String parentCommentId,
            String content,
            DataCallback<CommunityComment> callback
    ) {
        String cleaned = moderationService.sanitize(content);
        if (cleaned.isEmpty()) {
            callback.onError("Comment content cannot be empty.");
            return;
        }
        String userId = userRepository.currentUserId();
        String commentId = UUID.randomUUID().toString();
        CommunityComment comment = new CommunityComment(
                commentId,
                postId,
                parentCommentId,
                userId,
                anonymousIdentityService.displayNameForUser(userId),
                cleaned,
                timeProvider.nowMillis(),
                0
        );

        Map<String, Object> data = new HashMap<>();
        data.put("id", comment.id);
        data.put("postId", comment.postId);
        data.put("parentCommentId", comment.parentCommentId);
        data.put("authorUserId", comment.authorUserId);
        data.put("anonymousName", comment.anonymousName);
        data.put("content", comment.content);
        data.put("createdAt", comment.createdAt);
        data.put("likeCount", comment.likeCount);

        DocumentReference postRef = postsCollection().document(postId);
        DocumentReference commentRef = commentsCollection(postId).document(commentId);
        firestore.runTransaction((Transaction.Function<CommunityComment>) transaction -> {
            DocumentSnapshot postSnapshot = transaction.get(postRef);
            if (!postSnapshot.exists()) {
                throw new IllegalStateException("Post not found.");
            }
            transaction.set(commentRef, data);
            transaction.update(postRef, "commentCount", FieldValue.increment(1));
            return comment;
        }).addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to publish comment.")));
    }

    private List<CommunityPost> mapPosts(QuerySnapshot snapshot) {
        if (snapshot == null || snapshot.isEmpty()) {
            return Collections.emptyList();
        }
        List<CommunityPost> posts = new ArrayList<>();
        for (QueryDocumentSnapshot document : snapshot) {
            CommunityPost post = mapPost(document);
            if (post != null) {
                posts.add(post);
            }
        }
        return posts;
    }

    private List<CommunityComment> orderComments(QuerySnapshot snapshot) {
        if (snapshot == null || snapshot.isEmpty()) {
            return Collections.emptyList();
        }
        List<CommunityComment> parents = new ArrayList<>();
        Map<String, List<CommunityComment>> children = new HashMap<>();
        for (QueryDocumentSnapshot document : snapshot) {
            CommunityComment comment = mapComment(document);
            if (comment == null) {
                continue;
            }
            if (!comment.isReply()) {
                parents.add(comment);
                continue;
            }
            children.computeIfAbsent(comment.parentCommentId, ignored -> new ArrayList<>()).add(comment);
        }
        parents.sort(Comparator.comparingLong(comment -> comment.createdAt));
        for (List<CommunityComment> replies : children.values()) {
            replies.sort(Comparator.comparingLong(comment -> comment.createdAt));
        }
        List<CommunityComment> ordered = new ArrayList<>();
        for (CommunityComment parent : parents) {
            appendThread(parent, children, ordered);
        }
        return ordered;
    }

    private void appendThread(
            CommunityComment root,
            Map<String, List<CommunityComment>> children,
            List<CommunityComment> ordered
    ) {
        ordered.add(root);
        List<CommunityComment> replies = children.get(root.id);
        if (replies == null) {
            return;
        }
        for (CommunityComment reply : replies) {
            appendThread(reply, children, ordered);
        }
    }

    private void deletePostCascade(DocumentReference postRef, DataCallback<Boolean> callback) {
        commentsCollection(postRef.getId()).get()
                .addOnSuccessListener(commentSnapshot ->
                        postRef.collection(COLLECTION_LIKES).get()
                                .addOnSuccessListener(postLikesSnapshot -> {
                                    List<DocumentReference> refsToDelete = new ArrayList<>();
                                    refsToDelete.add(postRef);
                                    collectSnapshotReferences(postLikesSnapshot, refsToDelete);
                                    collectSnapshotReferences(commentSnapshot, refsToDelete);
                                    List<Task<QuerySnapshot>> commentLikeTasks = new ArrayList<>();
                                    if (commentSnapshot != null) {
                                        for (QueryDocumentSnapshot commentDocument : commentSnapshot) {
                                            commentLikeTasks.add(commentDocument.getReference().collection(COLLECTION_LIKES).get());
                                        }
                                    }
                                    if (commentLikeTasks.isEmpty()) {
                                        commitDeletes(refsToDelete, callback);
                                        return;
                                    }
                                    Tasks.whenAllSuccess(commentLikeTasks)
                                            .addOnSuccessListener(results -> {
                                                for (Object result : results) {
                                                    if (result instanceof QuerySnapshot) {
                                                        collectSnapshotReferences((QuerySnapshot) result, refsToDelete);
                                                    }
                                                }
                                                commitDeletes(refsToDelete, callback);
                                            })
                                            .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete post.")));
                                })
                                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete post."))))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete post.")));
    }

    private void collectSnapshotReferences(QuerySnapshot snapshot, List<DocumentReference> refsToDelete) {
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }
        for (QueryDocumentSnapshot document : snapshot) {
            refsToDelete.add(document.getReference());
        }
    }

    private void commitDeletes(List<DocumentReference> refsToDelete, DataCallback<Boolean> callback) {
        if (refsToDelete.size() > 450) {
            callback.onError("This post is too large to delete in one request.");
            return;
        }
        WriteBatch batch = firestore.batch();
        for (DocumentReference ref : refsToDelete) {
            batch.delete(ref);
        }
        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onError(readableError(e, "Failed to delete post.")));
    }

    private CommunityPost mapPost(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }
        return new CommunityPost(
                readString(document, "id", document.getId()),
                readString(document, "authorUserId", "guest"),
                readString(document, "anonymousName", "Anonymous"),
                readString(document, "content", ""),
                readString(document, "emotionTag", "Stress"),
                readLong(document, "createdAt"),
                readInt(document, "supportCount"),
                readInt(document, "likeCount"),
                readInt(document, "commentCount")
        );
    }

    private CommunityComment mapComment(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }
        return new CommunityComment(
                readString(document, "id", document.getId()),
                readString(document, "postId", ""),
                document.getString("parentCommentId"),
                readString(document, "authorUserId", "guest"),
                readString(document, "anonymousName", "Anonymous"),
                readString(document, "content", ""),
                readLong(document, "createdAt"),
                readInt(document, "likeCount")
        );
    }

    private String readString(DocumentSnapshot document, String field, String fallback) {
        String value = document.getString(field);
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private long readLong(DocumentSnapshot document, String field) {
        Long value = document.getLong(field);
        return value == null ? 0L : value;
    }

    private int readInt(DocumentSnapshot document, String field) {
        Long value = document.getLong(field);
        return value == null ? 0 : value.intValue();
    }

    private String normalizeTag(String emotionTag) {
        if (emotionTag == null || emotionTag.trim().isEmpty()) {
            return "Stress";
        }
        String trimmed = emotionTag.trim().replace("#", "");
        String[] parts = trimmed.split("\\s+");
        StringBuilder normalized = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (normalized.length() > 0) {
                normalized.append(' ');
            }
            normalized.append(part.substring(0, 1).toUpperCase(Locale.US));
            if (part.length() > 1) {
                normalized.append(part.substring(1).toLowerCase(Locale.US));
            }
        }
        return normalized.length() == 0 ? "Stress" : normalized.toString();
    }

    private CollectionReference postsCollection() {
        return firestore.collection(COLLECTION_POSTS);
    }

    private CollectionReference commentsCollection(String postId) {
        return postsCollection().document(postId).collection(COLLECTION_COMMENTS);
    }

    private DocumentReference commentDocument(String postId, String commentId) {
        return commentsCollection(postId).document(commentId);
    }

    private String readableError(@NonNull Exception exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.trim().isEmpty() ? fallback : message;
    }

    private static final class NoOpCallback<T> implements DataCallback<T> {
        @Override
        public void onSuccess(T data) {
        }

        @Override
        public void onError(String message) {
        }
    }
}
