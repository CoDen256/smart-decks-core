package coden.core.decks.revision;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class RevisionTest {

    @Test
    void testReminderReadProperly() throws IOException {
        final RevisionManagerImpl revisionManagerImpl = new RevisionManagerImpl(RevisionTest.class.getResourceAsStream("/revision_test.json"));

        assertEquals(10, revisionManagerImpl.getMaxLevel());
        assertEquals(0, revisionManagerImpl.getMinLevel());
        assertEquals(Duration.of(1, ChronoUnit.MINUTES), revisionManagerImpl.getTimeToNextRevision(0));
        assertEquals(Duration.of(1, ChronoUnit.HOURS), revisionManagerImpl.getTimeToNextRevision(1));

        assertEquals(Duration.of(1, ChronoUnit.DAYS), revisionManagerImpl.getTimeToNextRevision(2));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), revisionManagerImpl.getTimeToNextRevision(3));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), revisionManagerImpl.getTimeToNextRevision(4));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), revisionManagerImpl.getTimeToNextRevision(5));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), revisionManagerImpl.getTimeToNextRevision(6));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), revisionManagerImpl.getTimeToNextRevision(7));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), revisionManagerImpl.getTimeToNextRevision(8));

        assertEquals(ChronoUnit.WEEKS.getDuration(), revisionManagerImpl.getTimeToNextRevision(9));
        assertEquals(ChronoUnit.WEEKS.getDuration().multipliedBy(2), revisionManagerImpl.getTimeToNextRevision(10));
    }
}