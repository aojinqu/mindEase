package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mindease.data.local.entity.AnalysisSnapshotEntity;

@Dao
public interface AnalysisSnapshotDao {
    @Insert
    void insert(AnalysisSnapshotEntity entity);

    @Query("SELECT * FROM analysis_snapshots WHERE period_type = :periodType ORDER BY generated_at DESC LIMIT 1")
    AnalysisSnapshotEntity latest(String periodType);
}
