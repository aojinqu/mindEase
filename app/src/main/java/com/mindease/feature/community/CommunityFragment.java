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
import com.mindease.common.result.DataCallback;
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
        AppContainer container = appContainer();
        primaryLikeButton.setEnabled(false);
        viewModel.loadPosts(container, currentFilter, new DataCallback<List<CommunityPost>>() {
            @Override
            public void onSuccess(List<CommunityPost> posts) {
                if (!isAdded()) {
                    return;
                }
                currentPosts.clear();
                currentPosts.addAll(posts);
                adapter.notifyDataSetChanged();

                activeCountTextView.setText(posts.size() + " posts");
                hotTopicTextView.setText(posts.isEmpty() ? "Waiting" : posts.get(0).emotionTag);

                if (posts.isEmpty()) {
                    bindEmptyState();
                    return;
                }
                bindPrimaryPost(posts.get(0));
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                currentPosts.clear();
                adapter.notifyDataSetChanged();
                bindEmptyState();
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindEmptyState() {
        primaryTagTextView.setText("#" + currentFilter.toLowerCase(Locale.US));
        primaryContentTextView.setText("No posts yet");
        primaryMetaTextView.setText("Be the first to share");
        primarySupportTextView.setText("Support 0");
        primaryLikeTextView.setText("Like 0");
        primaryCommentTextView.setText("Comment 0");
        primaryLikeButton.setText("Like");
        primaryLikeButton.setEnabled(false);
    }

    private void bindPrimaryPost(CommunityPost post) {
        primaryTagTextView.setText("#" + post.emotionTag.toLowerCase(Locale.US));
        primaryContentTextView.setText(post.content);
        primaryMetaTextView.setText(post.anonymousName + "  " + dateFormat.format(post.createdAt));
        primarySupportTextView.setText("Support " + post.supportCount);
        primaryLikeTextView.setText("Like " + post.likeCount);
        primaryCommentTextView.setText("Comment " + post.commentCount);
        primaryLikeButton.setText("Checking...");
        primaryLikeButton.setEnabled(false);

        viewModel.hasLikedPost(appContainer(), post.id, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean hasLiked) {
                if (!isAdded()) {
                    return;
                }
                primaryLikeButton.setText(Boolean.TRUE.equals(hasLiked) ? "Liked" : "Like");
                primaryLikeButton.setEnabled(!Boolean.TRUE.equals(hasLiked));
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                primaryLikeButton.setText("Like");
                primaryLikeButton.setEnabled(true);
            }
        });
    }

    private void likePrimaryPost() {
        if (currentPosts.isEmpty()) {
            return;
        }
        likePost(currentPosts.get(0));
    }

    private void likePost(CommunityPost post) {
        primaryLikeButton.setEnabled(false);
        viewModel.likePost(appContainer(), post.id, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean liked) {
                if (!isAdded()) {
                    return;
                }
                if (!Boolean.TRUE.equals(liked)) {
                    Toast.makeText(requireContext(), "You already liked this post", Toast.LENGTH_SHORT).show();
                }
                bindPosts();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                bindPosts();
            }
        });
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

    private AppContainer appContainer() {
        return ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
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

            tagView.setText("#" + post.emotionTag.toLowerCase(Locale.US));
            metaView.setText(post.anonymousName + "  " + dateFormat.format(post.createdAt));
            contentView.setText(post.content);
            supportView.setText("Support " + post.supportCount);
            likeView.setText("Like " + post.likeCount);
            commentView.setText("Comment " + post.commentCount);
            likeButton.setText("Checking...");
            likeButton.setEnabled(false);

            viewModel.hasLikedPost(appContainer(), post.id, new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean hasLiked) {
                    likeButton.setText(Boolean.TRUE.equals(hasLiked) ? "Liked" : "Like");
                    likeButton.setEnabled(!Boolean.TRUE.equals(hasLiked));
                }

                @Override
                public void onError(String message) {
                    likeButton.setText("Like");
                    likeButton.setEnabled(true);
                }
            });

            likeButton.setOnClickListener(v -> likePost(post));
            commentButton.setOnClickListener(v -> openPostDetail(post));
            view.setOnClickListener(v -> openPostDetail(post));
            return view;
        }
    }
}
