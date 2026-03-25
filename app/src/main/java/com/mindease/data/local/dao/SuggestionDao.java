package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mindease.data.local.entity.SuggestionEntity;

@Dao
public interface SuggestionDao {
    @Insert
    void insert(SuggestionEntity entity);

    @Query("SELECT * FROM suggestions ORDER BY generated_at DESC LIMIT 1")
    SuggestionEntity latest();
}
