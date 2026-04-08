package com.mindease.feature.community;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.CommunityPost;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "extra_post_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        TextView tagTextView = findViewById(R.id.tv_detail_tag);
        TextView contentTextView = findViewById(R.id.tv_detail_content);
        TextView metaTextView = findViewById(R.id.tv_detail_meta);

        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        AppContainer container = ((MindEaseApp) getApplication()).getAppContainer();
        CommunityPost post = container.communityRepository.getPostById(postId);
        if (post == null) {
            tagTextView.setText("#unknown");
            contentTextView.setText("Post not found.");
            metaTextView.setText("");
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        tagTextView.setText("#" + post.emotionTag.toLowerCase(Locale.US));
        contentTextView.setText(post.content);
        metaTextView.setText(
                "posted " + format.format(post.createdAt)
                        + " | support " + post.supportCount
                        + " | like " + post.likeCount
        );
    }
}
