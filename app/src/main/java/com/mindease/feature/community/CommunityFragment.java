package com.mindease.feature.community;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.CommunityPost;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommunityFragment extends Fragment {
    private String currentFilter = "All";
    private TextView primaryTagTextView;
    private TextView primaryContentTextView;
    private TextView primaryMetaTextView;
    private TextView morePostsTextView;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.US);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        primaryTagTextView = view.findViewById(R.id.tv_community_primary_tag);
        primaryContentTextView = view.findViewById(R.id.tv_community_primary_content);
        primaryMetaTextView = view.findViewById(R.id.tv_community_primary_meta);
        morePostsTextView = view.findViewById(R.id.tv_community_more_posts);

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

        view.findViewById(R.id.btn_create_post).setOnClickListener(v -> showCreatePostDialog());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindPosts();
    }

    private void showCreatePostDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("Share your feeling anonymously...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(3);
        new AlertDialog.Builder(requireContext())
                .setTitle("Create Anonymous Post")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Post", (dialog, which) -> {
                    String content = input.getText() == null ? "" : input.getText().toString().trim();
                    if (content.isEmpty()) {
                        content = "I am trying to organize my emotions today.";
                    }
                    AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
                    container.communityRepository.createPost(content, currentFilter.equals("All") ? "Stress" : currentFilter);
                    bindPosts();
                })
                .show();
    }

    private void bindPosts() {
        AppContainer container = ((MindEaseApp) requireActivity().getApplication()).getAppContainer();
        List<CommunityPost> posts = container.communityRepository.listPostsByTag(currentFilter);
        if (posts.isEmpty()) {
            primaryTagTextView.setText("#" + currentFilter.toLowerCase(Locale.US));
            primaryContentTextView.setText("No posts under this filter yet.");
            primaryMetaTextView.setText("Create the first one.");
            morePostsTextView.setText("");
            return;
        }

        CommunityPost first = posts.get(0);
        primaryTagTextView.setText("#" + first.emotionTag.toLowerCase(Locale.US));
        primaryContentTextView.setText(first.content);
        primaryMetaTextView.setText(
                "posted " + dateFormat.format(first.createdAt)
                        + " · support " + first.supportCount
                        + " · like " + first.likeCount
        );

        if (posts.size() == 1) {
            morePostsTextView.setText("No more posts.");
            return;
        }
        StringBuilder builder = new StringBuilder("More posts:\n");
        int limit = Math.min(4, posts.size());
        for (int i = 1; i < limit; i++) {
            CommunityPost post = posts.get(i);
            builder.append("- #")
                    .append(post.emotionTag.toLowerCase(Locale.US))
                    .append(" · ")
                    .append(post.content)
                    .append("\n");
        }
        morePostsTextView.setText(builder.toString().trim());
    }
}
