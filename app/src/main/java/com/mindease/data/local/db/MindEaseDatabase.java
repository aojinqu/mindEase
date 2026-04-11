package com.mindease.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mindease.data.local.dao.AnalysisSnapshotDao;
import com.mindease.data.local.dao.AgentMessageDao;
import com.mindease.data.local.dao.AgentSessionDao;
import com.mindease.data.local.dao.MoodRecordDao;
import com.mindease.data.local.dao.MoodRecordTagDao;
import com.mindease.data.local.dao.MoodTagDao;
import com.mindease.data.local.dao.SuggestionDao;
import com.mindease.data.local.entity.AnalysisSnapshotEntity;
import com.mindease.data.local.entity.AgentMessageEntity;
import com.mindease.data.local.entity.AgentSessionEntity;
import com.mindease.data.local.entity.MoodRecordEntity;
import com.mindease.data.local.entity.MoodRecordTagCrossRef;
import com.mindease.data.local.entity.MoodTagEntity;
import com.mindease.data.local.entity.SuggestionEntity;

@Database(
        entities = {
                MoodRecordEntity.class,
                MoodTagEntity.class,
                MoodRecordTagCrossRef.class,
                SuggestionEntity.class,
                AnalysisSnapshotEntity.class,
                AgentSessionEntity.class,
                AgentMessageEntity.class
        },
        version = 2,
        exportSchema = false
)
public abstract class MindEaseDatabase extends RoomDatabase {
    public abstract MoodRecordDao moodRecordDao();

    public abstract MoodTagDao moodTagDao();

    public abstract MoodRecordTagDao moodRecordTagDao();

    public abstract SuggestionDao suggestionDao();

    public abstract AnalysisSnapshotDao analysisSnapshotDao();

    public abstract AgentSessionDao agentSessionDao();

    public abstract AgentMessageDao agentMessageDao();
}
