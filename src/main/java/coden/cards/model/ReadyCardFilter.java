package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.reminder.Reminder;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.function.Predicate;

public class ReadyCardFilter implements Predicate<Card> {

    private final Reminder reminder;

    public ReadyCardFilter(Reminder reminder) {
        this.reminder = reminder;
    }

    @Override
    public boolean test(Card card) {
        final Instant lastReview = card.getLastReview();
        final int level = card.getLevel();
        final TemporalAmount nextReminderDelay = reminder.getNextReminderDelay(level);
        final Instant nextRemind = lastReview.plus(nextReminderDelay);
        return !Instant.now().isBefore(nextRemind);
    }
}
