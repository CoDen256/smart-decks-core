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
        assertEquals(Duration.of(1, ChronoUnit.MINUTES), reminder.getNextReminderDelay(0));
        assertEquals(Duration.of(1, ChronoUnit.HOURS), reminder.getNextReminderDelay(1));

        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getNextReminderDelay(2));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getNextReminderDelay(3));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getNextReminderDelay(4));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getNextReminderDelay(5));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getNextReminderDelay(6));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getNextReminderDelay(7));
        assertEquals(Duration.of(1, ChronoUnit.DAYS), reminder.getNextReminderDelay(8));

        assertEquals(ChronoUnit.WEEKS.getDuration(), reminder.getNextReminderDelay(9));
        assertEquals(ChronoUnit.WEEKS.getDuration().multipliedBy(2), reminder.getNextReminderDelay(10));
    }
}