package com.mindease.feature.mood;

import android.text.TextUtils;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.AnalysisReport;
import com.mindease.domain.model.MoodRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoodEditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_editor);

        AppContainer container = ((MindEaseApp) getApplication()).getAppContainer();
        ChipGroup moodGroup = findViewById(R.id.chip_group_mood);
        ChipGroup tagsGroup = findViewById(R.id.chip_group_tags);
        TextInputEditText diaryEditText = findViewById(R.id.et_diary);
        android.widget.SeekBar intensitySeek = findViewById(R.id.seek_intensity);
        MaterialButton saveButton = findViewById(R.id.btn_save_mood);

        saveButton.setOnClickListener(v -> {
            String mood = selectedChipText(moodGroup, "Calm");
            List<String> tags = selectedTags(tagsGroup);
            String diaryText = diaryEditText.getText() == null ? "" : diaryEditText.getText().toString().trim();
            int intensity = intensitySeek.getProgress() + 1;

            MoodRecord record = new MoodRecord(
                    UUID.randomUUID().toString(),
                    mood,
                    intensity,
                    diaryText,
                    tags,
                    System.currentTimeMillis()
            );

            container.createMoodRecordUseCase.execute(record);
            AnalysisReport report = container.generateMoodAnalysisUseCase.execute(7);
            container.generateSuggestionUseCase.execute(report);

            Toast.makeText(this, R.string.msg_mood_saved, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }

    private String selectedChipText(ChipGroup chipGroup, String fallback) {
        int checkedId = chipGroup.getCheckedChipId();
        if (checkedId == -1) {
            return fallback;
        }
        Chip chip = chipGroup.findViewById(checkedId);
        if (chip == null || TextUtils.isEmpty(chip.getText())) {
            return fallback;
        }
        return chip.getText().toString();
    }

    private List<String> selectedTags(ChipGroup chipGroup) {
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            if (!(chipGroup.getChildAt(i) instanceof Chip)) {
                continue;
            }
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                tags.add(chip.getText().toString());
            }
        }
        return tags;
    }
}
