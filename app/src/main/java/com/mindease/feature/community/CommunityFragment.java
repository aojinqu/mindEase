package com.mindease.feature.community;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.common.result.DataCallback;
import com.mindease.domain.model.CommunityPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CommunityFragment extends Fragment {
    private static final String FILTER_ALL = "All";
    private static final String FILTER_ANXIOUS = "Anxious";
    private static final String FILTER_STRESS = "Stress";
    private static final String FILTER_SLEEP = "Sleep";

    private String currentFilter = FILTER_ALL;
    private TextView hotTopicTextView;
    private TextView activeCountTextView;
    private TextView primaryNameTextView;
    private TextView primaryTagTextView;
    private TextView primaryContentTextView;
    private TextView primaryMetaTextView;
    private TextView primaryLikeTextView;
    private TextView primaryCommentTextView;
    private ImageButton primaryLikeButton;
    private ListView postsListView;
    private ChipGroup filterChipGroup;
    private final List<CommunityPost> currentPosts = new ArrayList<>();
    private final Set<String> availableTags = new LinkedHashSet<>();
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
        primaryNameTextView = view.findViewById(R.id.tv_community_primary_name);
        primaryTagTextView = view.findViewById(R.id.tv_community_primary_tag);
        primaryContentTextView = view.findViewById(R.id.tv_community_primary_content);
        primaryMetaTextView = view.findViewById(R.id.tv_community_primary_meta);
        primaryLikeTextView = view.findViewById(R.id.tv_community_primary_like);
        primaryCommentTextView = view.findViewById(R.id.tv_community_primary_comment);
        primaryLikeButton = view.findViewById(R.id.btn_primary_like);
        postsListView = view.findViewById(R.id.lv_community_posts);
        filterChipGroup = view.findViewById(R.id.chip_group_community_filter);
        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        adapter = new CommunityPostAdapter();
        postsListView.setAdapter(adapter);
        seedDefaultTags();
        renderFilterChips();

        postEditorLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        bindPosts();
                    }
                }
        );

        view.findViewById(R.id.btn_create_post).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PostEditorActivity.class);
            postEditorLauncher.launch(intent);
        });
        view.findViewById(R.id.card_community_primary).setOnClickListener(v -> openPrimaryPostDetail());
        view.findViewById(R.id.btn_primary_comment).setOnClickListener(v -> openPrimaryPostDetail());
        view.findViewById(R.id.layout_primary_like).setOnClickListener(v -> togglePrimaryPostLike());
        primaryLikeButton.setOnClickListener(v -> togglePrimaryPostLike());

        postsListView.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position < 0 || position >= adapter.getCount()) {
                return;
            }
            openPostDetail(currentPosts.get(position + 1));
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

    private void seedDefaultTags() {
        availableTags.clear();
        availableTags.add(FILTER_ALL);
        availableTags.add(FILTER_ANXIOUS);
        availableTags.add(FILTER_STRESS);
        availableTags.add(FILTER_SLEEP);
    }

    private void syncAvailableTags(List<CommunityPost> posts) {
        boolean changed = false;
        for (CommunityPost post : posts) {
            String tag = post.emotionTag == null ? "" : post.emotionTag.trim();
            if (!tag.isEmpty()) {
                changed |= availableTags.add(tag);
            }
        }
        if (!availableTags.contains(currentFilter)) {
            changed |= availableTags.add(currentFilter);
        }
        if (changed) {
            renderFilterChips();
        } else {
            checkCurrentFilterChip();
        }
    }

    private void renderFilterChips() {
        if (filterChipGroup == null) {
            return;
        }
        filterChipGroup.removeAllViews();
        for (String tag : availableTags) {
            filterChipGroup.addView(buildFilterChip(tag));
        }
        checkCurrentFilterChip();
    }

    private Chip buildFilterChip(String tag) {
        Chip chip = new Chip(requireContext());
        chip.setText(tag);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setId(View.generateViewId());
        chip.setChipBackgroundColorResource(R.color.chip_filter_background);
        chip.setChipStrokeColorResource(R.color.chip_filter_stroke);
        chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_filter_text));
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> onFilterChecked(isChecked, tag));
        return chip;
    }

    private void checkCurrentFilterChip() {
        if (filterChipGroup == null) {
            return;
        }
        for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
            View child = filterChipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChecked(currentFilter.equals(chip.getText().toString()));
            }
        }
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
                syncAvailableTags(posts);
                adapter.notifyDataSetChanged();
                updatePostsListHeight();

                activeCountTextView.setText(posts.size() + " Posts");
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
                updatePostsListHeight();
                bindEmptyState();
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindEmptyState() {
        primaryNameTextView.setText("Anonymous");
        primaryTagTextView.setText("#" + currentFilter.toUpperCase(Locale.US));
        primaryContentTextView.setText("No posts yet");
        primaryMetaTextView.setText("Be the first to share");
        primaryLikeTextView.setText("0");
        primaryCommentTextView.setText("0");
        primaryLikeButton.setImageResource(R.drawable.ic_community_heart_outline);
        primaryLikeButton.setEnabled(false);
    }

    private void bindPrimaryPost(CommunityPost post) {
        primaryNameTextView.setText(post.anonymousName);
        primaryTagTextView.setText("#" + post.emotionTag.toUpperCase(Locale.US));
        primaryContentTextView.setText(post.content);
        primaryMetaTextView.setText(dateFormat.format(post.createdAt));
        primaryLikeTextView.setText(String.valueOf(post.likeCount));
        primaryCommentTextView.setText(String.valueOf(post.commentCount));
        applyLikeVisualState(
                view().findViewById(R.id.layout_primary_like),
                primaryLikeButton,
                primaryLikeTextView,
                false
        );
        primaryLikeButton.setEnabled(false);

        viewModel.hasLikedPost(appContainer(), post.id, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean hasLiked) {
                if (!isAdded()) {
                    return;
                }
                applyLikeVisualState(
                        view().findViewById(R.id.layout_primary_like),
                        primaryLikeButton,
                        primaryLikeTextView,
                        Boolean.TRUE.equals(hasLiked)
                );
                primaryLikeButton.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                applyLikeVisualState(
                        view().findViewById(R.id.layout_primary_like),
                        primaryLikeButton,
                        primaryLikeTextView,
                        false
                );
                primaryLikeButton.setEnabled(true);
            }
        });
    }

    private void togglePrimaryPostLike() {
        if (currentPosts.isEmpty()) {
            return;
        }
        togglePostLike(currentPosts.get(0));
    }

    private void togglePostLike(CommunityPost post) {
        primaryLikeButton.setEnabled(false);
        viewModel.togglePostLike(appContainer(), post.id, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean liked) {
                if (!isAdded()) {
                    return;
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

    private void updatePostsListHeight() {
        if (postsListView.getAdapter() == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, postsListView);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    requireView().getWidth(),
                    View.MeasureSpec.AT_MOST
            );
            listItem.measure(widthMeasureSpec, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = postsListView.getLayoutParams();
        params.height = totalHeight + (postsListView.getDividerHeight() * Math.max(adapter.getCount() - 1, 0));
        postsListView.setLayoutParams(params);
        postsListView.requestLayout();
    }

    private final class CommunityPostAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Math.max(currentPosts.size() - 1, 0);
        }

        @Override
        public Object getItem(int position) {
            return currentPosts.get(position + 1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = convertView == null
                    ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post, parent, false)
                    : convertView;

            CommunityPost post = currentPosts.get(position + 1);
            view.setTag(post.id);
            TextView nameView = view.findViewById(R.id.tv_item_post_name);
            TextView tagView = view.findViewById(R.id.tv_item_post_tag);
            TextView metaView = view.findViewById(R.id.tv_item_post_meta);
            TextView contentView = view.findViewById(R.id.tv_item_post_content);
            TextView likeView = view.findViewById(R.id.tv_item_post_like);
            TextView commentView = view.findViewById(R.id.tv_item_post_comment);
            ImageButton likeButton = view.findViewById(R.id.btn_item_post_like);
            View likeLayout = view.findViewById(R.id.layout_item_post_like);
            View commentButton = view.findViewById(R.id.btn_item_post_comment);

            nameView.setText(post.anonymousName);
            tagView.setText("#" + post.emotionTag.toUpperCase(Locale.US));
            metaView.setText(dateFormat.format(post.createdAt));
            contentView.setText(post.content);
            likeView.setText(String.valueOf(post.likeCount));
            commentView.setText(String.valueOf(post.commentCount));
            applyLikeVisualState(likeLayout, likeButton, likeView, false);
            likeButton.setEnabled(false);

            viewModel.hasLikedPost(appContainer(), post.id, new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean hasLiked) {
                    Object boundPostId = view.getTag();
                    if (boundPostId == null || !post.id.equals(boundPostId)) {
                        return;
                    }
                    applyLikeVisualState(likeLayout, likeButton, likeView, Boolean.TRUE.equals(hasLiked));
                    likeButton.setEnabled(true);
                }

                @Override
                public void onError(String message) {
                    Object boundPostId = view.getTag();
                    if (boundPostId == null || !post.id.equals(boundPostId)) {
                        return;
                    }
                    applyLikeVisualState(likeLayout, likeButton, likeView, false);
                    likeButton.setEnabled(true);
                }
            });

            likeButton.setOnClickListener(v -> {
                likeButton.setEnabled(false);
                viewModel.togglePostLike(appContainer(), post.id, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean liked) {
                        if (!isAdded()) {
                            return;
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
            });
            commentButton.setOnClickListener(v -> openPostDetail(post));
            view.setOnClickListener(v -> openPostDetail(post));
            return view;
        }
    }

    private View view() {
        return requireView();
    }

    private void applyLikeVisualState(View container, ImageButton button, TextView countView, boolean liked) {
        int backgroundColor = ContextCompat.getColor(requireContext(),
                liked ? R.color.md_theme_light_onPrimaryContainer : android.R.color.white);
        int foregroundColor = ContextCompat.getColor(requireContext(),
                liked ? android.R.color.white : R.color.md_theme_light_onSurface);
        container.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        button.setImageResource(liked ? R.drawable.ic_community_heart_filled : R.drawable.ic_community_heart_outline);
        button.setImageTintList(ColorStateList.valueOf(foregroundColor));
        countView.setTextColor(foregroundColor);
    }
}
