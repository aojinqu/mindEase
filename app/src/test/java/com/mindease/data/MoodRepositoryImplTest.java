package com.mindease.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mindease.data.repository.MoodRepositoryImpl;
import com.mindease.domain.model.MoodRecord;
import com.mindease.domain.service.TimeProvider;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MoodRepositoryImplTest {

    @Test
    public void createUpdateDeleteAndRecent_shouldWork() {
        long now = 1_000_000L;
        MoodRepositoryImpl repo = new MoodRepositoryImpl(new FixedTimeProvider(now));

        MoodRecord oldRecord = record("1", "sad", 2, "stressed for exam", now - daysToMillis(10), "exam");
        MoodRecord recentRecord = record("2", "calm", 4, "good day", now - daysToMillis(2), "sleep");
        repo.create(oldRecord);
        repo.create(recentRecord);

        List<MoodRecord> recent7 = repo.getRecent(7);
        assertEquals(1, recent7.size());
        assertEquals("2", recent7.get(0).id);

        MoodRecord updated = record("2", "happy", 5, "great progress today", now - daysToMillis(1), "study");
        repo.update(updated);
        List<MoodRecord> recent30 = repo.getRecent(30);
        assertEquals("happy", recent30.get(0).moodType);

        repo.delete("2");
        assertTrue(repo.getRecent(30).stream().noneMatch(r -> "2".equals(r.id)));
    }

    private MoodRecord record(String id, String mood, int intensity, String text, long createdAt, String... tags) {
        return new MoodRecord(id, mood, intensity, text, Arrays.asList(tags), createdAt);
    }

    private long daysToMillis(int days) {
        return days * 24L * 60L * 60L * 1000L;
    }

    private static class FixedTimeProvider implements TimeProvider {
        private final long now;

        private FixedTimeProvider(long now) {
            this.now = now;
        }

        @Override
        public long nowMillis() {
            return now;
        }
    }
}
