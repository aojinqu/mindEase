package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mindease.data.local.entity.MoodRecordEntity;

import java.util.List;

@Dao
public interface MoodRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoodRecordEntity entity);

    @Update
    void update(MoodRecordEntity entity);

    @Delete
    void delete(MoodRecordEntity entity);

    @Query("SELECT * FROM mood_records WHERE user_id = :userId ORDER BY created_at DESC")
    List<MoodRecordEntity> findAllByUserId(String userId);

    @Query("SELECT * FROM mood_records WHERE user_id = :userId AND created_at >= :from ORDER BY created_at DESC")
    List<MoodRecordEntity> findRecentByUserId(String userId, long from);

    @Query("SELECT * FROM mood_records WHERE record_id = :recordId AND user_id = :userId LIMIT 1")
    MoodRecordEntity findByIdAndUserId(String recordId, String userId);

    @Query("DELETE FROM mood_records WHERE record_id = :recordId AND user_id = :userId")
    int deleteByIdAndUserId(String recordId, String userId);

    @Query("SELECT * FROM mood_records ORDER BY created_at DESC")
    List<MoodRecordEntity> findAll();
}
