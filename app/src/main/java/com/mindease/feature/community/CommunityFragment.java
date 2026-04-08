package com.mindease.feature.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
    private TextView primaryTagTextView;
    private TextView primaryContentTextView;
    private TextView primaryMetaTextView;
    private ListView postsListView;
    private final List<CommunityPost> currentPosts = new ArrayList<>();
    private final List<String> postLabels = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
    private CommunityViewModel viewModel;
    private ActivityResultLauncher<Intent> postEditorLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        primaryTagTextView = view.findViewById(R.id.tv_community_primary_tag);
        primaryContentTextView = view.findViewById(R.id.tv_community_primary_content);
        primaryMetaTextView = view.findViewById(R.id.tv_community_primary_meta);
        postsListView = view.findViewById(R.id.lv_community_posts);
        viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);

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

        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "All";
                bindPosts();
            }
        });
        chipAnxious.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "Anxious";
                bindPosts();
            }
        });
        chipStress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "Stress";
                bindPosts();
            }
        });
        chipSleep.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentFilter = "Sleep";
                bindPosts();
            }
        });

        view.findViewById(R.id.btn_create_post).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PostEditorActivity.class);
            postEditorLauncher.launch(intent);
        });

        view.findViewById(R.id.btn_open_detail).setOnClickListener(v -> openPrimaryPostDetail());
        view.findViewById(R.id.card_community_primary).setOnClickListener(v -> openPrimaryPostDetail());

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

    private void bindPosts() {
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        List<CommunityPost> posts = viewModel.loadPosts(container, currentFilter);

        currentPosts.clear();
        currentPosts.addAll(posts);

        if (posts.isEmpty()) {
            primaryTagTextView.setText("#" + currentFilter.toLowerCase(Locale.US));
            primaryContentTextView.setText("No posts under this filter yet.");
            primaryMetaTextView.setText("Create the first one.");
            postLabels.clear();
            postsListView.setAdapter(new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    postLabels
            ));
            return;
        }

        CommunityPost first = posts.get(0);
        primaryTagTextView.setText("#" + first.emotionTag.toLowerCase(Locale.US));
        primaryContentTextView.setText(first.content);
        primaryMetaTextView.setText(
                "posted " + dateFormat.format(first.createdAt)
                        + " | support " + first.supportCount
                        + " | like " + first.likeCount
        );

        postLabels.clear();
        for (CommunityPost post : posts) {
            postLabels.add(
                    "#" + post.emotionTag + " | "
                            + post.content
                            + " | " + dateFormat.format(post.createdAt)
            );
        }
        postsListView.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                postLabels
        ));
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
}
