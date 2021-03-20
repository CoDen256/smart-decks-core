package coden.core.decks.reminder;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class ReminderTest {

    @Test
    void testReminderReadProperly() throws IOException {
        final Reminder reminder = new Reminder(ReminderTest.class.getResourceAsStream("/reminder_test.json"));

        assertEquals(10, reminder.getMaxLevel());
        assertEquals(0, reminder.getMinLevel());
        assertEquals(Duration.of(1, ChronoUnit.MINUTES), reminder.getTimeToNextRevision(0));
        assertEquals(Duration.of(1, ChronoUnit.HOURS), reminder.getTimeToNextRevision(1));

        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getTimeToNextRevision(2));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getTimeToNextRevision(3));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getTimeToNextRevision(4));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getTimeToNextRevision(5));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getTimeToNextRevision(6));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getTimeToNextRevision(7));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getTimeToNextRevision(8));

        assertEquals(ChronoUnit.WEEKS.getDuration(), reminder.getTimeToNextRevision(9));
        assertEquals(ChronoUnit.WEEKS.getDuration().multipliedBy(2), reminder.getTimeToNextRevision(10));
    }
}