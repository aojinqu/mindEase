package com.mindease.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mindease.data.local.entity.AgentMessageEntity;

import java.util.List;

@Dao
public interface AgentMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AgentMessageEntity entity);

    @Query("SELECT * FROM agent_messages WHERE session_id = :sessionId ORDER BY created_at ASC")
    List<AgentMessageEntity> findAllBySessionId(String sessionId);

    @Query("SELECT * FROM agent_messages WHERE session_id = :sessionId ORDER BY created_at DESC LIMIT :limit")
    List<AgentMessageEntity> findLatestBySessionId(String sessionId, int limit);
}
