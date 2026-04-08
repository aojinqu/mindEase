package com.mindease.feature.mood;

import android.text.TextUtils;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mindease.R;
import com.mindease.app.AppContainer;
import com.mindease.app.MindEaseApp;
import com.mindease.domain.model.MoodRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoodEditorActivity extends AppCompatActivity {
    private MoodEditorViewModel viewModel;
    private AppContainer container;
    private ChipGroup moodGroup;
    private ChipGroup tagsGroup;
    private TextInputEditText diaryEditText;
    private TextInputEditText customMoodEditText;
    private android.widget.SeekBar intensitySeek;
    private TextView intensityValueText;
    private ListView recentListView;
    private MoodRecord selectedRecord;
    private final List<MoodRecord> recentRecords = new ArrayList<>();
    private final List<String> recentRecordLabels = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_editor);

        viewModel = new ViewModelProvider(this).get(MoodEditorViewModel.class);
        container = ((MindEaseApp) getApplication()).getAppContainer();
        moodGroup = findViewById(R.id.chip_group_mood);
        tagsGroup = findViewById(R.id.chip_group_tags);
        diaryEditText = findViewById(R.id.et_diary);
        customMoodEditText = findViewById(R.id.et_custom_mood);
        intensitySeek = findViewById(R.id.seek_intensity);
        intensityValueText = findViewById(R.id.tv_intensity_value);
        recentListView = findViewById(R.id.lv_recent_records);
        MaterialButton saveButton = findViewById(R.id.btn_save_mood);
        MaterialButton updateButton = findViewById(R.id.btn_update_mood);
        MaterialButton deleteButton = findViewById(R.id.btn_delete_mood);
        MaterialButton clearSelectionButton = findViewById(R.id.btn_clear_selection);
        MaterialButton addCustomMoodButton = findViewById(R.id.btn_add_custom_mood);

        saveButton.setOnClickListener(v -> createRecord());
        updateButton.setOnClickListener(v -> updateSelectedRecord());
        deleteButton.setOnClickListener(v -> deleteSelectedRecord());
        clearSelectionButton.setOnClickListener(v -> clearSelection());
        addCustomMoodButton.setOnClickListener(v -> addCustomMoodFromInput());

        recentListView.setOnItemClickListener((parent, view, position, id) -> selectRecord(position));
        intensitySeek.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                updateIntensityLabel(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });
        updateIntensityLabel(getSelectedIntensity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadRecentRecords();
    }

    private void createRecord() {
        MoodRecord record = viewModel.create(
                selectedChipText(moodGroup, "Calm"),
                getSelectedIntensity(),
                getDiaryText(),
                selectedTags(tagsGroup)
        );
        viewModel.createRecord(container, record);
        Toast.makeText(this, R.string.msg_mood_saved, Toast.LENGTH_SHORT).show();
        clearForm();
        reloadRecentRecords();
    }

    private void updateSelectedRecord() {
        if (selectedRecord == null) {
            Toast.makeText(this, "Select a record to update", Toast.LENGTH_SHORT).show();
            return;
        }
        MoodRecord updated = new MoodRecord(
                selectedRecord.id,
                selectedChipText(moodGroup, selectedRecord.moodType),
                getSelectedIntensity(),
                getDiaryText(),
                selectedTags(tagsGroup),
                selectedRecord.createdAt
        );
        viewModel.updateRecord(container, updated);
        Toast.makeText(this, "Record updated", Toast.LENGTH_SHORT).show();
        clearSelection();
        reloadRecentRecords();
    }

    private void deleteSelectedRecord() {
        if (selectedRecord == null) {
            Toast.makeText(this, "Select a record to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.deleteRecord(container, selectedRecord.id);
        Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show();
        clearSelection();
        reloadRecentRecords();
    }

    private void reloadRecentRecords() {
        recentRecords.clear();
        recentRecords.addAll(viewModel.loadRecent(container));
        recentRecordLabels.clear();
        for (MoodRecord record : recentRecords) {
            recentRecordLabels.add(
                    dateFormat.format(record.createdAt)
                            + " | " + record.moodType
                            + " (" + record.moodIntensity + ")"
                            + " | " + preview(record.diaryText)
            );
        }
        recentListView.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, recentRecordLabels)
        );
    }

    private void selectRecord(int position) {
        if (position < 0 || position >= recentRecords.size()) {
            return;
        }
        selectedRecord = recentRecords.get(position);
        applySelectionToForm(selectedRecord);
        recentListView.setItemChecked(position, true);
    }

    private void clearSelection() {
        selectedRecord = null;
        recentListView.clearChoices();
        clearForm();
    }

    private void clearForm() {
        intensitySeek.setProgress(2);
        diaryEditText.setText("");
        customMoodEditText.setText("");
        clearChipChecks(moodGroup);
        clearChipChecks(tagsGroup);
        updateIntensityLabel(getSelectedIntensity());
    }

    private void applySelectionToForm(MoodRecord record) {
        setSelectedChipByText(moodGroup, record.moodType);
        setCheckedTags(record.tags);
        intensitySeek.setProgress(Math.max(0, Math.min(4, record.moodIntensity - 1)));
        diaryEditText.setText(record.diaryText);
    }

    private void setCheckedTags(List<String> tags) {
        clearChipChecks(tagsGroup);
        for (int i = 0; i < tagsGroup.getChildCount(); i++) {
            if (!(tagsGroup.getChildAt(i) instanceof Chip)) {
                continue;
            }
            Chip chip = (Chip) tagsGroup.getChildAt(i);
            chip.setChecked(tags.contains(chip.getText().toString()));
        }
    }

    private void clearChipChecks(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            if (chipGroup.getChildAt(i) instanceof Chip) {
                ((Chip) chipGroup.getChildAt(i)).setChecked(false);
            }
        }
    }

    private void setSelectedChipByText(ChipGroup chipGroup, String text) {
        boolean matched = false;
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            if (!(chipGroup.getChildAt(i) instanceof Chip)) {
                continue;
            }
            Chip chip = (Chip) chipGroup.getChildAt(i);
            boolean shouldCheck = text.equalsIgnoreCase(chip.getText().toString());
            chip.setChecked(shouldCheck);
            matched = matched || shouldCheck;
        }
        if (!matched && chipGroup == moodGroup && !TextUtils.isEmpty(text)) {
            addMoodChipIfMissing(text, true);
        }
    }

    private String preview(String diaryText) {
        if (diaryText == null || diaryText.trim().isEmpty()) {
            return "No notes";
        }
        String trimmed = diaryText.trim();
        return trimmed.length() <= 28 ? trimmed : trimmed.substring(0, 28) + "...";
    }

    private String getDiaryText() {
        return diaryEditText.getText() == null ? "" : diaryEditText.getText().toString().trim();
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

    private void addCustomMoodFromInput() {
        String customMood = customMoodEditText.getText() == null
                ? ""
                : customMoodEditText.getText().toString().trim();
        if (TextUtils.isEmpty(customMood)) {
            Toast.makeText(this, "Please input a custom mood", Toast.LENGTH_SHORT).show();
            return;
        }
        addMoodChipIfMissing(customMood, true);
        customMoodEditText.setText("");
    }

    private void addMoodChipIfMissing(String moodText, boolean select) {
        for (int i = 0; i < moodGroup.getChildCount(); i++) {
            if (!(moodGroup.getChildAt(i) instanceof Chip)) {
                continue;
            }
            Chip chip = (Chip) moodGroup.getChildAt(i);
            if (!moodText.equalsIgnoreCase(chip.getText().toString())) {
                continue;
            }
            if (select) {
                chip.setChecked(true);
            }
            return;
        }
        Chip chip = new Chip(this);
        chip.setText(moodText);
        chip.setCheckable(true);
        chip.setLayoutParams(new ChipGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        moodGroup.addView(chip);
        if (select) {
            chip.setChecked(true);
        }
    }

    private int getSelectedIntensity() {
        return intensitySeek.getProgress() + 1;
    }

    private void updateIntensityLabel(int intensity) {
        intensityValueText.setText("Intensity: " + intensity + "/5");
    }
}
