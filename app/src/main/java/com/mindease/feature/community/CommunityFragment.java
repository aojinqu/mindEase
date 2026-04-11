package com.mindease.feature.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.CommunityPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommunityFragment extends Fragment {
    private String currentFilter = "All";
    private TextView hotTopicTextView;
    private TextView activeCountTextView;
    private TextView primaryTagTextView;
    private TextView primaryContentTextView;
    private TextView primaryMetaTextView;
    private TextView primarySupportTextView;
    private TextView primaryLikeTextView;
    private TextView primaryCommentTextView;
    private MaterialButton primaryLikeButton;
    private ListView postsListView;
    private final List<CommunityPost> currentPosts = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
    private CommunityViewModel viewModel;
    private ActivityResultLauncher<Intent> postEditorLauncher;
    private CommunityPostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        hotTopicTextView = view.findViewById(R.id.tv_community_hot_topic);
        activeCountTextView = view.findViewById(R.id.tv_community_active_count);
        primaryTagTextView = view.findViewById(R.id.tv_community_primary_tag);
        primaryContentTextView = view.findViewById(R.id.tv_community_primary_content);
        primaryMetaTextView = view.findViewById(R.id.tv_community_primary_meta);
        primarySupportTextView = view.findViewById(R.id.tv_community_primary_support);
        primaryLikeTextView = view.findViewById(R.id.tv_community_primary_like);
        primaryCommentTextView = view.findViewById(R.id.tv_community_primary_comment);
        primaryLikeButton = view.findViewById(R.id.btn_primary_like);
        postsListView = view.findViewById(R.id.lv_community_posts);
        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        adapter = new CommunityPostAdapter();
        postsListView.setAdapter(adapter);

        postEditorLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        bindPosts();
                    }
                }
        );

        Chip chipAll = view.findViewById(R.id.chip_filter_all);
        Chip chipAnxious = view.findViewById(R.id.chip_filter_anxious);
        Chip chipStress = view.findViewById(R.id.chip_filter_stress);
        Chip chipSleep = view.findViewById(R.id.chip_filter_sleep);

        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> onFilterChecked(isChecked, "All"));
        chipAnxious.setOnCheckedChangeListener((buttonView, isChecked) -> onFilterChecked(isChecked, "Anxious"));
        chipStress.setOnCheckedChangeListener((buttonView, isChecked) -> onFilterChecked(isChecked, "Stress"));
        chipSleep.setOnCheckedChangeListener((buttonView, isChecked) -> onFilterChecked(isChecked, "Sleep"));

        view.findViewById(R.id.btn_create_post).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PostEditorActivity.class);
            postEditorLauncher.launch(intent);
        });
        view.findViewById(R.id.card_community_primary).setOnClickListener(v -> openPrimaryPostDetail());
        view.findViewById(R.id.btn_primary_comment).setOnClickListener(v -> openPrimaryPostDetail());
        primaryLikeButton.setOnClickListener(v -> likePrimaryPost());

        postsListView.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position < 0 || position >= currentPosts.size()) {
                return;
            }
            openPostDetail(currentPosts.get(position));
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindPosts();
    }

    private void onFilterChecked(boolean isChecked, String filter) {
        if (!isChecked) {
            return;
        }
        currentFilter = filter;
        bindPosts();
    }

    private void bindPosts() {
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        List<CommunityPost> posts = viewModel.loadPosts(container, currentFilter);

        currentPosts.clear();
        currentPosts.addAll(posts);
        adapter.notifyDataSetChanged();

        activeCountTextView.setText(posts.size() + " posts");
        hotTopicTextView.setText(posts.isEmpty() ? "Waiting" : posts.get(0).emotionTag);

        if (posts.isEmpty()) {
            primaryTagTextView.setText("#" + currentFilter.toLowerCase(Locale.US));
            primaryContentTextView.setText("No posts yet");
            primaryMetaTextView.setText("Be the first to share");
            primarySupportTextView.setText("🫶 0");
            primaryLikeTextView.setText("❤ 0");
            primaryCommentTextView.setText("💬 0");
            primaryLikeButton.setText("Like");
            primaryLikeButton.setEnabled(false);
            return;
        }

        CommunityPost first = posts.get(0);
        bindPrimaryPost(first);
    }

    private void bindPrimaryPost(CommunityPost post) {
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        boolean hasLiked = viewModel.hasLikedPost(container, post.id);
        primaryTagTextView.setText("#" + post.emotionTag.toLowerCase(Locale.US));
        primaryContentTextView.setText(post.content);
        primaryMetaTextView.setText(post.anonymousName + "  " + dateFormat.format(post.createdAt));
        primarySupportTextView.setText("🫶 " + post.supportCount);
        primaryLikeTextView.setText("❤ " + post.likeCount);
        primaryCommentTextView.setText("💬 " + post.commentCount);
        primaryLikeButton.setText(hasLiked ? "Liked" : "Like");
        primaryLikeButton.setEnabled(!hasLiked);
    }

    private void likePrimaryPost() {
        if (currentPosts.isEmpty()) {
            return;
        }
        likePost(currentPosts.get(0));
    }

    private void likePost(CommunityPost post) {
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        if (!viewModel.likePost(container, post.id)) {
            String message = viewModel.hasLikedPost(container, post.id)
                    ? "You already liked this post"
                    : "Failed to like post";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }
        bindPosts();
    }

    private void openPrimaryPostDetail() {
        if (currentPosts.isEmpty()) {
            return;
        }
        openPostDetail(currentPosts.get(0));
    }

    private void openPostDetail(CommunityPost post) {
        Intent intent = new Intent(requireContext(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.id);
        startActivity(intent);
    }

    private final class CommunityPostAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return currentPosts.size();
        }

        @Override
        public Object getItem(int position) {
            return currentPosts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post, parent, false);
            }

            CommunityPost post = currentPosts.get(position);
            TextView tagView = view.findViewById(R.id.tv_item_post_tag);
            TextView metaView = view.findViewById(R.id.tv_item_post_meta);
            TextView contentView = view.findViewById(R.id.tv_item_post_content);
            TextView supportView = view.findViewById(R.id.tv_item_post_support);
            TextView likeView = view.findViewById(R.id.tv_item_post_like);
            TextView commentView = view.findViewById(R.id.tv_item_post_comment);
            MaterialButton likeButton = view.findViewById(R.id.btn_item_post_like);
            MaterialButton commentButton = view.findViewById(R.id.btn_item_post_comment);
            AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
            boolean hasLiked = viewModel.hasLikedPost(container, post.id);

            tagView.setText("#" + post.emotionTag.toLowerCase(Locale.US));
            metaView.setText(post.anonymousName + "  " + dateFormat.format(post.createdAt));
            contentView.setText(post.content);
            supportView.setText("🫶 " + post.supportCount);
            likeView.setText("❤ " + post.likeCount);
            commentView.setText("💬 " + post.commentCount);
            likeButton.setText(hasLiked ? "Liked" : "Like");
            likeButton.setEnabled(!hasLiked);

            likeButton.setOnClickListener(v -> likePost(post));
            commentButton.setOnClickListener(v -> openPostDetail(post));
            view.setOnClickListener(v -> openPostDetail(post));
            return view;
        }
    }
}
