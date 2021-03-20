package coden.core.decks.reminder;

import coden.core.decks.data.Card;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

public interface BaseReminder{
    int getMinLevel();

    int getMaxLevel();

    TemporalAmount getNextReminderDelay(int level);

    boolean shouldRemind(Card card);

    Duration getOvertime(Card card);
}
