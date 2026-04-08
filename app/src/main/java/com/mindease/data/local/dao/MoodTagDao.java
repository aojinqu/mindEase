package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mindease.data.local.entity.MoodTagEntity;

import java.util.List;

@Dao
public interface MoodTagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<MoodTagEntity> tags);

    @Query("SELECT * FROM mood_tags")
    List<MoodTagEntity> findAll();
}
