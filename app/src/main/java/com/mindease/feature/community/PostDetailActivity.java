package com.mindease.feature.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.model.CommunityPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "extra_post_id";
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    private TextView tagTextView;
    private TextView contentTextView;
    private TextView metaTextView;
    private TextView replyingTextView;
    private TextInputEditText commentEditText;
    private ListView commentsListView;
    private MaterialButton likePostButton;
    private MaterialButton likeCommentButton;
    private CommunityViewModel viewModel;
    private AppContainer container;
    private String postId;
    private String replyingToCommentId;
    private String selectedCommentId;
    private final List<CommunityComment> commentItems = new ArrayList<>();
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tagTextView = findViewById(R.id.tv_detail_tag);
        contentTextView = findViewById(R.id.tv_detail_content);
        metaTextView = findViewById(R.id.tv_detail_meta);
        replyingTextView = findViewById(R.id.tv_detail_replying);
        commentEditText = findViewById(R.id.et_detail_comment);
        commentsListView = findViewById(R.id.lv_detail_comments);
        likePostButton = findViewById(R.id.btn_detail_like);
        MaterialButton sendButton = findViewById(R.id.btn_detail_send);
        likeCommentButton = findViewById(R.id.btn_detail_like_comment);
        MaterialButton deleteCommentButton = findViewById(R.id.btn_detail_delete_comment);

        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        container = ((MindEaseApp) getApplication()).getAppContainer();
        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        commentAdapter = new CommentAdapter();
        commentsListView.setAdapter(commentAdapter);

        CommunityPost post = container.communityRepository.getPostById(postId);
        if (post == null) {
            tagTextView.setText("#unknown");
            contentTextView.setText("Post not found.");
            metaTextView.setText("");
            return;
        }

        likePostButton.setOnClickListener(v -> {
            if (!viewModel.likePost(container, postId)) {
                String message = viewModel.hasLikedPost(container, postId)
                        ? "You already liked this post"
                        : "Failed to like post.";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                return;
            }
            refreshPostAndComments();
        });
        sendButton.setOnClickListener(v -> submitCommentOrReply());
        likeCommentButton.setOnClickListener(v -> likeSelectedComment());
        deleteCommentButton.setOnClickListener(v -> deleteSelectedComment());
        replyingTextView.setOnClickListener(v -> clearSelection());

        refreshPostAndComments();
    }

    private void refreshPostAndComments() {
        CommunityPost post = container.communityRepository.getPostById(postId);
        if (post == null) {
            tagTextView.setText("#unknown");
            contentTextView.setText("Post not found.");
            metaTextView.setText("");
            return;
        }

        tagTextView.setText("#" + post.emotionTag.toLowerCase(Locale.US));
        contentTextView.setText(post.content);
        metaTextView.setText(
                post.anonymousName + "  " + format.format(post.createdAt)
                        + "  Like " + post.likeCount
                        + "  Comment " + post.commentCount
        );
        boolean hasLikedPost = viewModel.hasLikedPost(container, postId);
        likePostButton.setText(hasLikedPost ? "Liked" : "Like");
        likePostButton.setEnabled(!hasLikedPost);

        List<CommunityComment> comments = viewModel.loadComments(container, postId);
        commentItems.clear();
        commentItems.addAll(comments);
        commentAdapter.notifyDataSetChanged();
        updateCommentActionState();
    }

    private void submitCommentOrReply() {
        String text = commentEditText.getText() == null ? "" : commentEditText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Please input comment content.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommunityComment result;
        if (replyingToCommentId == null) {
            result = viewModel.addComment(container, postId, text);
        } else {
            result = viewModel.replyToComment(container, postId, replyingToCommentId, text);
        }
        if (result == null) {
            Toast.makeText(this, "Failed to publish comment.", Toast.LENGTH_SHORT).show();
            return;
        }

        commentEditText.setText("");
        clearSelection();
        refreshPostAndComments();
    }

    private void likeSelectedComment() {
        if (selectedCommentId == null) {
            Toast.makeText(this, "Select a comment first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!viewModel.likeComment(container, postId, selectedCommentId)) {
            String message = viewModel.hasLikedComment(container, postId, selectedCommentId)
                    ? "You already liked this comment"
                    : "Failed to like comment.";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }
        refreshPostAndComments();
    }

    private void deleteSelectedComment() {
        if (selectedCommentId == null) {
            Toast.makeText(this, "Select a comment first.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean deleted = viewModel.deleteComment(container, postId, selectedCommentId);
        if (!deleted) {
            Toast.makeText(this, "Delete failed: only author can delete, and parent with replies cannot be deleted.", Toast.LENGTH_SHORT).show();
            return;
        }
        clearSelection();
        refreshPostAndComments();
    }

    private void clearSelection() {
        replyingToCommentId = null;
        selectedCommentId = null;
        replyingTextView.setText("");
        updateCommentActionState();
    }

    private void updateCommentActionState() {
        boolean hasSelection = selectedCommentId != null;
        boolean alreadyLiked = hasSelection && viewModel.hasLikedComment(container, postId, selectedCommentId);
        likeCommentButton.setEnabled(hasSelection && !alreadyLiked);
        likeCommentButton.setText(alreadyLiked ? "Liked" : "Like Comment");
    }

    private final class CommentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return commentItems.size();
        }

        @Override
        public Object getItem(int position) {
            return commentItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_comment, parent, false);
            }

            CommunityComment comment = commentItems.get(position);
            String currentUserId = container.userRepository.currentUserId();
            TextView authorView = view.findViewById(R.id.tv_item_comment_author);
            TextView badgeView = view.findViewById(R.id.tv_item_comment_badge);
            TextView timeView = view.findViewById(R.id.tv_item_comment_time);
            TextView contentView = view.findViewById(R.id.tv_item_comment_content);
            TextView likeView = view.findViewById(R.id.tv_item_comment_like);
            MaterialButton likeButton = view.findViewById(R.id.btn_item_comment_like);
            MaterialButton replyButton = view.findViewById(R.id.btn_item_comment_reply);
            MaterialButton deleteButton = view.findViewById(R.id.btn_item_comment_delete);

            String mine = currentUserId.equals(comment.authorUserId) ? "  • You" : "";
            authorView.setText(comment.anonymousName + mine);
            badgeView.setText(comment.isReply() ? "Reply" : "Comment");
            timeView.setText(format.format(comment.createdAt));
            contentView.setText(comment.content);
            likeView.setText("Like " + comment.likeCount);
            boolean hasLiked = viewModel.hasLikedComment(container, postId, comment.id);
            likeButton.setText(hasLiked ? "Liked" : "Like");
            likeButton.setEnabled(!hasLiked);

            likeButton.setOnClickListener(v -> {
                selectedCommentId = comment.id;
                updateCommentActionState();
                likeSelectedComment();
            });
            replyButton.setOnClickListener(v -> {
                selectedCommentId = comment.id;
                replyingToCommentId = comment.id;
                replyingTextView.setText("Replying to " + comment.anonymousName + "  Tap to cancel");
                updateCommentActionState();
                commentEditText.requestFocus();
            });
            deleteButton.setOnClickListener(v -> {
                selectedCommentId = comment.id;
                updateCommentActionState();
                deleteSelectedComment();
            });
            deleteButton.setVisibility(currentUserId.equals(comment.authorUserId) ? View.VISIBLE : View.GONE);

            int leftPadding = comment.isReply() ? dpToPx(18) : dpToPx(0);
            view.setPadding(leftPadding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            return view;
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
