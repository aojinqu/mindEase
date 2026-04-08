package com.mindease.feature.community;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;

public class PostEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_editor);

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
            container.communityRepository.createPost(content, tag);
            setResult(RESULT_OK);
            finish();
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
