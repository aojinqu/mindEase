package com.mindease.data.repository;

import com.mindease.data.local.dao.MoodRecordDao;
import com.mindease.data.local.dao.MoodRecordTagDao;
import com.mindease.data.local.dao.MoodTagDao;
import com.mindease.data.local.entity.MoodRecordEntity;
import com.mindease.data.local.entity.MoodRecordTagCrossRef;
import com.mindease.data.local.entity.MoodTagEntity;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.repository.MoodRepository;
import com.mindease.domain.repository.UserRepository;
import com.mindease.domain.service.SystemTimeProvider;
import com.mindease.domain.service.TimeProvider;

import java.util.ArrayList;
import java.util.List;

public class RoomMoodRepository implements MoodRepository {
    private final MoodRecordDao moodRecordDao;
    private final MoodTagDao moodTagDao;
    private final MoodRecordTagDao moodRecordTagDao;
    private final UserRepository userRepository;
    private final TimeProvider timeProvider;

    public RoomMoodRepository(
            MoodRecordDao moodRecordDao,
            MoodTagDao moodTagDao,
            MoodRecordTagDao moodRecordTagDao,
            UserRepository userRepository
    ) {
        this(moodRecordDao, moodTagDao, moodRecordTagDao, userRepository, new SystemTimeProvider());
    }

    public RoomMoodRepository(
            MoodRecordDao moodRecordDao,
            MoodTagDao moodTagDao,
            MoodRecordTagDao moodRecordTagDao,
            UserRepository userRepository,
            TimeProvider timeProvider
    ) {
        this.moodRecordDao = moodRecordDao;
        this.moodTagDao = moodTagDao;
        this.moodRecordTagDao = moodRecordTagDao;
        this.userRepository = userRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    public void create(MoodRecord record) {
        String userId = userRepository.currentUserId();
        moodRecordDao.insert(toEntity(record, userId));
        replaceTags(record.id, record.tags);
    }

    @Override
    public void update(MoodRecord record) {
        String userId = userRepository.currentUserId();
        MoodRecordEntity existing = moodRecordDao.findByIdAndUserId(record.id, userId);
        if (existing == null) {
            throw new IllegalArgumentException("Mood record not found: " + record.id);
        }
        moodRecordDao.update(toEntity(record, userId));
        replaceTags(record.id, record.tags);
    }

    @Override
    public void delete(String recordId) {
        String userId = userRepository.currentUserId();
        moodRecordTagDao.deleteByRecordId(recordId);
        moodRecordDao.deleteByIdAndUserId(recordId, userId);
    }

    @Override
    public List<MoodRecord> getRecent(int days) {
        long from = timeProvider.nowMillis() - (days * 24L * 60L * 60L * 1000L);
        String userId = userRepository.currentUserId();
        List<MoodRecordEntity> entities = moodRecordDao.findRecentByUserId(userId, from);
        List<MoodRecord> records = new ArrayList<>();
        for (MoodRecordEntity entity : entities) {
            List<String> tags = moodRecordTagDao.findTagNamesByRecordId(entity.recordId);
            records.add(toDomain(entity, tags));
        }
        return records;
    }

    private MoodRecordEntity toEntity(MoodRecord record, String userId) {
        MoodRecordEntity entity = new MoodRecordEntity();
        entity.recordId = record.id;
        entity.userId = userId;
        entity.moodType = record.moodType;
        entity.moodIntensity = record.moodIntensity;
        entity.diaryText = record.diaryText;
        entity.createdAt = record.createdAt;
        return entity;
    }

    private MoodRecord toDomain(MoodRecordEntity entity, List<String> tags) {
        return new MoodRecord(
                entity.recordId,
                entity.moodType,
                entity.moodIntensity,
                entity.diaryText,
                tags,
                entity.createdAt
        );
    }

    private void replaceTags(String recordId, List<String> tags) {
        moodRecordTagDao.deleteByRecordId(recordId);
        if (tags == null || tags.isEmpty()) {
            return;
        }

        List<MoodTagEntity> tagEntities = new ArrayList<>();
        List<MoodRecordTagCrossRef> refs = new ArrayList<>();
        for (String rawTag : tags) {
            if (rawTag == null) {
                continue;
            }
            String tagName = rawTag.trim();
            if (tagName.isEmpty()) {
                continue;
            }
            String tagId = "tag_" + tagName.toLowerCase();
            MoodTagEntity tagEntity = new MoodTagEntity();
            tagEntity.tagId = tagId;
            tagEntity.tagName = tagName;
            tagEntity.tagCategory = "general";
            tagEntities.add(tagEntity);

            MoodRecordTagCrossRef ref = new MoodRecordTagCrossRef();
            ref.recordId = recordId;
            ref.tagId = tagId;
            refs.add(ref);
        }

        if (!tagEntities.isEmpty()) {
            moodTagDao.insertAll(tagEntities);
        }
        if (!refs.isEmpty()) {
            moodRecordTagDao.insertAll(refs);
        }
    }
}
