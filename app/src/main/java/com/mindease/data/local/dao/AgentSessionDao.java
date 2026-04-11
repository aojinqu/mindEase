package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mindease.data.local.entity.AgentSessionEntity;

import java.util.List;

@Dao
public interface AgentSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AgentSessionEntity entity);

    @Update
    void update(AgentSessionEntity entity);

    @Query("SELECT * FROM agent_sessions WHERE session_id = :sessionId AND user_id = :userId LIMIT 1")
    AgentSessionEntity findByIdAndUserId(String sessionId, String userId);

    @Query("SELECT * FROM agent_sessions WHERE user_id = :userId ORDER BY updated_at DESC")
    List<AgentSessionEntity> findAllByUserId(String userId);
}
