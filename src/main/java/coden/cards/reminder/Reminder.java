package coden.cards.reminder;

import coden.cards.data.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Reminder implements BaseReminder {

    private final List<ReminderLevel> levels = new LinkedList<>();

    public Reminder(InputStream is) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ReminderLevelEntry[] entries = objectMapper.readValue(Objects.requireNonNull(is), ReminderLevelEntry[].class);

        for (ReminderLevelEntry entry: entries){
            for (int level: entry.getLevels()){
                addLevel(entry, level);
            }
        }
    }

    private void addLevel(ReminderLevelEntry entry, int level) throws IOException {
        final TemporalAmount amount = parseTemporalAmount(entry.getUnit(), entry.getAmount());
        final ReminderLevel newLevel = new ReminderLevel(level, amount);
        if (levels.contains(newLevel)) throw new IOException("Found two reminder level entries with the same levels");
        levels.add(newLevel);
    }

    private TemporalAmount parseTemporalAmount(String unit, int amount) throws IOException{
        final ChronoUnit chronoUnit = ChronoUnit.valueOf(unit);
        return chronoUnit.getDuration().multipliedBy(amount);
    }

    @VisibleForTesting
    TemporalAmount getNextReminderDelay(int level){
        return levels.stream()
                .filter(entry -> entry.getLevel() == level)
                .findAny()
                .map(ReminderLevel::getTemporalAmount)
                .orElseThrow();
    }

    @Override
    public int getMinLevel(){
        return levels.stream()
                .min(Comparator.comparing(ReminderLevel::getLevel))
                .map(ReminderLevel::getLevel)
                .orElse(0);
    }

    @Override
    public int getMaxLevel(){
        return levels.stream()
                .max(Comparator.comparing(ReminderLevel::getLevel))
                .map(ReminderLevel::getLevel)
                .orElse(0);
    }

    @Override
    public boolean shouldRemind(Card card) {
        final Instant nextReminder = getNextReminder(card);
        return !Instant.now().isBefore(nextReminder);
    }

    @Override
    public Duration getOvertime(Card card){
        final Instant nextReminder = getNextReminder(card);
        final Instant now = Instant.now();
        return Duration.between(nextReminder, now);
    }

    private Instant getNextReminder(Card card) {
        final Instant lastReview = card.getLastReview();
        final int level = card.getLevel();
        final TemporalAmount nextReminderDelay = this.getNextReminderDelay(level);
        return lastReview.plus(nextReminderDelay);
    }

}
