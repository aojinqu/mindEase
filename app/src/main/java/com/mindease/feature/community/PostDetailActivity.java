package com.mindease.feature.community;

import android.os.Bundle;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.common.result.DataCallback;
import com.mindease.domain.model.CommunityComment;
import com.mindease.domain.model.CommunityPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "extra_post_id";
    private final SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.US);

    private TextView nameTextView;
    private TextView tagTextView;
    private TextView contentTextView;
    private TextView metaTextView;
    private TextView likeCountTextView;
    private TextView commentCountTextView;
    private TextView replyingTextView;
    private TextInputEditText commentEditText;
    private ListView commentsListView;
    private ImageButton likePostButton;
    private MaterialButton deletePostButton;
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

        nameTextView = findViewById(R.id.tv_detail_name);
        tagTextView = findViewById(R.id.tv_detail_tag);
        contentTextView = findViewById(R.id.tv_detail_content);
        metaTextView = findViewById(R.id.tv_detail_meta);
        likeCountTextView = findViewById(R.id.tv_detail_like_count);
        commentCountTextView = findViewById(R.id.tv_detail_comment_count);
        replyingTextView = findViewById(R.id.tv_detail_replying);
        commentEditText = findViewById(R.id.et_detail_comment);
        commentsListView = findViewById(R.id.lv_detail_comments);
        likePostButton = findViewById(R.id.btn_detail_like);
        deletePostButton = findViewById(R.id.btn_detail_delete_post);
        MaterialButton sendButton = findViewById(R.id.btn_detail_send);
        MaterialButton deleteCommentButton = findViewById(R.id.btn_detail_delete_comment);

        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        container = ((MindEaseApp) getApplication()).getAppContainer();
        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        commentAdapter = new CommentAdapter();
        commentsListView.setAdapter(commentAdapter);

        findViewById(R.id.layout_detail_like).setOnClickListener(v -> togglePostLike());
        likePostButton.setOnClickListener(v -> togglePostLike());
        deletePostButton.setOnClickListener(v -> confirmDeletePost());
        sendButton.setOnClickListener(v -> submitCommentOrReply());
        deleteCommentButton.setOnClickListener(v -> deleteSelectedComment());
        replyingTextView.setOnClickListener(v -> clearSelection());

        refreshPostAndComments();
    }

    private void refreshPostAndComments() {
        viewModel.getPost(container, postId, new DataCallback<CommunityPost>() {
            @Override
            public void onSuccess(CommunityPost post) {
                bindPost(post);
                loadComments();
            }

            @Override
            public void onError(String message) {
                nameTextView.setText("Anonymous");
                tagTextView.setText("#UNKNOWN");
                contentTextView.setText("Post not found.");
                metaTextView.setText("");
                likeCountTextView.setText("0");
                commentCountTextView.setText("0");
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindPost(CommunityPost post) {
        boolean isMine = container.userRepository.currentUserId().equals(post.authorUserId);
        nameTextView.setText(isMine ? post.anonymousName + "  You" : post.anonymousName);
        tagTextView.setText("#" + post.emotionTag.toUpperCase(Locale.US));
        contentTextView.setText(post.content);
        metaTextView.setText(format.format(post.createdAt));
        likeCountTextView.setText(String.valueOf(post.likeCount));
        commentCountTextView.setText(String.valueOf(post.commentCount));
        deletePostButton.setVisibility(isMine ? View.VISIBLE : View.GONE);
        deletePostButton.setEnabled(isMine);
        applyLikeVisualState(findViewById(R.id.layout_detail_like), likePostButton, likeCountTextView, false);
        likePostButton.setEnabled(false);
        viewModel.hasLikedPost(container, postId, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean hasLikedPost) {
                applyLikeVisualState(
                        findViewById(R.id.layout_detail_like),
                        likePostButton,
                        likeCountTextView,
                        Boolean.TRUE.equals(hasLikedPost)
                );
                likePostButton.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                applyLikeVisualState(
                        findViewById(R.id.layout_detail_like),
                        likePostButton,
                        likeCountTextView,
                        false
                );
                likePostButton.setEnabled(true);
            }
        });
    }

    private void confirmDeletePost() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete this post?")
                .setMessage("This will remove the post and all comments under it.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deletePost())
                .show();
    }

    private void deletePost() {
        deletePostButton.setEnabled(false);
        viewModel.deletePost(container, postId, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean deleted) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                deletePostButton.setEnabled(true);
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        viewModel.loadComments(container, postId, new DataCallback<List<CommunityComment>>() {
            @Override
            public void onSuccess(List<CommunityComment> comments) {
                commentItems.clear();
                commentItems.addAll(comments);
                commentAdapter.notifyDataSetChanged();
                updateCommentsListHeight();
            }

            @Override
            public void onError(String message) {
                commentItems.clear();
                commentAdapter.notifyDataSetChanged();
                updateCommentsListHeight();
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitCommentOrReply() {
        String text = commentEditText.getText() == null ? "" : commentEditText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Please input comment content.", Toast.LENGTH_SHORT).show();
            return;
        }

        DataCallback<CommunityComment> callback = new DataCallback<CommunityComment>() {
            @Override
            public void onSuccess(CommunityComment result) {
                commentEditText.setText("");
                clearSelection();
                refreshPostAndComments();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };

        if (replyingToCommentId == null) {
            viewModel.addComment(container, postId, text, callback);
        } else {
            viewModel.replyToComment(container, postId, replyingToCommentId, text, callback);
        }
    }

    private void togglePostLike() {
        likePostButton.setEnabled(false);
        viewModel.togglePostLike(container, postId, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean liked) {
                refreshPostAndComments();
            }

            @Override
            public void onError(String message) {
                likePostButton.setEnabled(true);
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSelectedComment() {
        if (selectedCommentId == null) {
            Toast.makeText(this, "Select a comment first.", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.deleteComment(container, postId, selectedCommentId, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean deleted) {
                clearSelection();
                refreshPostAndComments();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSelection() {
        replyingToCommentId = null;
        selectedCommentId = null;
        replyingTextView.setText("");
        replyingTextView.setVisibility(View.GONE);
    }

    private void updateCommentsListHeight() {
        if (commentsListView.getAdapter() == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < commentAdapter.getCount(); i++) {
            View listItem = commentAdapter.getView(i, null, commentsListView);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    getResources().getDisplayMetrics().widthPixels - dpToPx(40),
                    View.MeasureSpec.AT_MOST
            );
            listItem.measure(widthMeasureSpec, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = commentsListView.getLayoutParams();
        params.height = totalHeight + (commentsListView.getDividerHeight() * Math.max(commentAdapter.getCount() - 1, 0));
        commentsListView.setLayoutParams(params);
        commentsListView.requestLayout();
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
            final View view = convertView == null
                    ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_comment, parent, false)
                    : convertView;

            CommunityComment comment = commentItems.get(position);
            String currentUserId = container.userRepository.currentUserId();
            view.setTag(comment.id);
            TextView authorView = view.findViewById(R.id.tv_item_comment_author);
            TextView badgeView = view.findViewById(R.id.tv_item_comment_badge);
            TextView timeView = view.findViewById(R.id.tv_item_comment_time);
            TextView contentView = view.findViewById(R.id.tv_item_comment_content);
            TextView likeView = view.findViewById(R.id.tv_item_comment_like);
            ImageButton likeButton = view.findViewById(R.id.btn_item_comment_like);
            View likeLayout = view.findViewById(R.id.layout_item_comment_like);
            MaterialButton replyButton = view.findViewById(R.id.btn_item_comment_reply);
            MaterialButton deleteButton = view.findViewById(R.id.btn_item_comment_delete);

            String mine = currentUserId.equals(comment.authorUserId) ? "  You" : "";
            authorView.setText(comment.anonymousName + mine);
            badgeView.setText(comment.isReply() ? "REPLY" : "COMMENT");
            timeView.setText(format.format(comment.createdAt));
            contentView.setText(comment.content);
            likeView.setText(String.valueOf(comment.likeCount));
            applyLikeVisualState(likeLayout, likeButton, likeView, false);
            likeButton.setEnabled(false);

            viewModel.hasLikedComment(container, postId, comment.id, new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean hasLiked) {
                    Object boundCommentId = view.getTag();
                    if (boundCommentId == null || !comment.id.equals(boundCommentId)) {
                        return;
                    }
                    applyLikeVisualState(likeLayout, likeButton, likeView, Boolean.TRUE.equals(hasLiked));
                    likeButton.setEnabled(true);
                }

                @Override
                public void onError(String message) {
                    Object boundCommentId = view.getTag();
                    if (boundCommentId == null || !comment.id.equals(boundCommentId)) {
                        return;
                    }
                    applyLikeVisualState(likeLayout, likeButton, likeView, false);
                    likeButton.setEnabled(true);
                }
            });

            likeButton.setOnClickListener(v -> {
                likeButton.setEnabled(false);
                selectedCommentId = comment.id;
                viewModel.toggleCommentLike(container, postId, comment.id, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean liked) {
                        refreshPostAndComments();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        refreshPostAndComments();
                    }
                });
            });
            replyButton.setOnClickListener(v -> {
                selectedCommentId = comment.id;
                replyingToCommentId = comment.id;
                replyingTextView.setText("Replying to " + comment.anonymousName + "  Tap to cancel");
                replyingTextView.setVisibility(View.VISIBLE);
                commentEditText.requestFocus();
            });
            deleteButton.setOnClickListener(v -> {
                selectedCommentId = comment.id;
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

    private void applyLikeVisualState(View container, ImageButton button, TextView countView, boolean liked) {
        int backgroundColor = ContextCompat.getColor(this,
                liked ? R.color.md_theme_light_onPrimaryContainer : android.R.color.white);
        int foregroundColor = ContextCompat.getColor(this,
                liked ? android.R.color.white : R.color.md_theme_light_onSurface);
        container.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        button.setImageResource(liked ? R.drawable.ic_community_heart_filled : R.drawable.ic_community_heart_outline);
        button.setImageTintList(ColorStateList.valueOf(foregroundColor));
        countView.setTextColor(foregroundColor);
    }
}
