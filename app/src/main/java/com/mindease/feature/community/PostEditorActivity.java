package com.mindease.feature.community;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.common.result.DataCallback;
import com.mindease.domain.model.CommunityPost;

public class PostEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_editor);

        CommunityViewModel viewModel = new ViewModelProvider(this).get(CommunityViewModel.class);
        ChipGroup chipGroup = findViewById(R.id.chip_group_post_tag);
        TextInputEditText contentEditText = findViewById(R.id.et_post_content);
        findViewById(R.id.btn_publish_post).setOnClickListener(v -> {
            String content = contentEditText.getText() == null ? "" : contentEditText.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Please enter post content", Toast.LENGTH_SHORT).show();
                return;
            }
            String tag = selectedTag(chipGroup);
            AppContainer container = ((MindEaseApp) getApplication()).getAppContainer();
            v.setEnabled(false);
            viewModel.createPost(container, content, tag, new DataCallback<CommunityPost>() {
                @Override
                public void onSuccess(CommunityPost data) {
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String message) {
                    v.setEnabled(true);
                    Toast.makeText(PostEditorActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String selectedTag(ChipGroup chipGroup) {
        int checkedId = chipGroup.getCheckedChipId();
        if (checkedId == -1) {
            return "Stress";
        }
        Chip chip = chipGroup.findViewById(checkedId);
        return chip == null ? "Stress" : chip.getText().toString();
    }
}
