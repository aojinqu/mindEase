package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mindease.data.local.entity.MoodRecordTagCrossRef;

import java.util.List;

@Dao
public interface MoodRecordTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MoodRecordTagCrossRef> refs);

    @Query("DELETE FROM mood_record_tag_cross_ref WHERE record_id = :recordId")
    void deleteByRecordId(String recordId);

    @Query("SELECT t.tag_name FROM mood_tags t INNER JOIN mood_record_tag_cross_ref c ON t.tag_id = c.tag_id WHERE c.record_id = :recordId")
    List<String> findTagNamesByRecordId(String recordId);
}
