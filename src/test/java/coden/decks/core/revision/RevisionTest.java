package coden.decks.core.revision;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import coden.decks.core.data.Card;
import coden.decks.core.data.SimpleCard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RevisionTest {

    private static RevisionManagerImpl revisor;

    private static final InputStream CONFIG = RevisionTest.class.getResourceAsStream("/revision_test.json");
    private static final Instant BASE = Instant.now();

    private static final Duration MINUTE = Duration.of(1, ChronoUnit.MINUTES);
    private static final Duration HOUR = Duration.of(1, ChronoUnit.HOURS);
    private static final Duration WEEK = ChronoUnit.WEEKS.getDuration();
    private static final Duration DAY = Duration.of(1, ChronoUnit.DAYS);

    @BeforeAll
    static void beforeAll() throws IOException {
        revisor = new RevisionManagerImpl(CONFIG);
    }

    @Test
    void testGetTimeToNextRevision_Level() throws IOException {
        assertEquals(MINUTE, revisor.getTimeToNextRevision(0));
        assertEquals(HOUR, revisor.getTimeToNextRevision(1));

        assertEquals(DAY, revisor.getTimeToNextRevision(2));
        assertEquals(DAY, revisor.getTimeToNextRevision(3));
        assertEquals(DAY, revisor.getTimeToNextRevision(4));
        assertEquals(DAY, revisor.getTimeToNextRevision(5));
        assertEquals(DAY, revisor.getTimeToNextRevision(6));
        assertEquals(DAY, revisor.getTimeToNextRevision(7));
        assertEquals(DAY, revisor.getTimeToNextRevision(8));

        assertEquals(WEEK, revisor.getTimeToNextRevision(9));
        assertEquals(WEEK.multipliedBy(2), revisor.getTimeToNextRevision(10));
    }

    @Test
    void getTimeToNextRevision_Card() {
        Card card = getCard(3);
        Instant nextRevision = BASE.plus(DAY);
        Duration expectedDuration = Duration.between(BASE, nextRevision);
        Duration actualDuration = revisor.getTimeToNextRevision(card);
        assertTrue(Math.abs(actualDuration.compareTo(expectedDuration)) < 1000);
    }

    @Test
    void testGetNextRevision() {
        assertEquals(BASE.plus(MINUTE), revisor.getNextRevision(getCard(0)));
        assertEquals(BASE.plus(HOUR), revisor.getNextRevision(getCard(1)));
        assertEquals(BASE.plus(DAY), revisor.getNextRevision(getCard(2)));
        assertEquals(BASE.plus(WEEK), revisor.getNextRevision(getCard(9)));
        assertEquals(BASE.plus(WEEK.multipliedBy(2)), revisor.getNextRevision(getCard(10)));
    }

    @Test
    void testIsReady() {
        Instant weekAgo = BASE.minus(WEEK).minus(DAY);
        assertTrue(revisor.isReady(getCard(0, weekAgo)));
        assertTrue(revisor.isReady(getCard(1, weekAgo)));
        assertTrue(revisor.isReady(getCard(2, weekAgo)));
        assertTrue(revisor.isReady(getCard(9, weekAgo)));
        assertFalse(revisor.isReady(getCard(10, weekAgo)));
    }

    private Card getCard(int level) {
        return getCard(level, BASE);
    }

    private Card getCard(int level, Instant base) {
        return new SimpleCard.Builder()
                .setFrontSide("front")
                .setBackSide("back")
                .setLastReview(base)
                .setLevel(level)
                .create();
    }

    @Test
    void testGetMaxMinLevel() throws IOException {
        assertEquals(10, revisor.getMaxLevel());
        assertEquals(0, revisor.getMinLevel());
    }
}