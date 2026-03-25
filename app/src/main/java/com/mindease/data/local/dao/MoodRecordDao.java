package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mindease.data.local.entity.MoodRecordEntity;

import java.util.List;

@Dao
public interface MoodRecordDao {
    @Insert
    void insert(MoodRecordEntity entity);

    @Update
    void update(MoodRecordEntity entity);

    @Delete
    void delete(MoodRecordEntity entity);

    @Query("SELECT * FROM mood_records ORDER BY created_at DESC")
    List<MoodRecordEntity> findAll();
}
