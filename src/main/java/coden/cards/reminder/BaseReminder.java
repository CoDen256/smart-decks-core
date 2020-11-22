package coden.cards.reminder;

import coden.cards.data.Card;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.function.Predicate;

public interface BaseReminder{
    int getMinLevel();

    int getMaxLevel();

    TemporalAmount getNextReminderDelay(int level);

    boolean shouldRemind(Card card);

    Duration getOvertime(Card card);
}
