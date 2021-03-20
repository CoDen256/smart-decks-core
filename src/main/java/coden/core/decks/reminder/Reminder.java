package coden.core.decks.reminder;

import coden.core.decks.data.Card;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        for (ReminderLevelEntry entry : entries) {
            for (int level : entry.getLevels()) {
                addLevel(entry, level);
            }
        }
    }

    private void addLevel(ReminderLevelEntry entry, int level) throws IOException {
        final Duration amount = Duration.parse(entry.getDurationString());
        final ReminderLevel newLevel = new ReminderLevel(level, amount);
        if (levels.contains(newLevel)) throw new IOException("Found two reminder level entries with the same levels");
        levels.add(newLevel);
    }

    /**
     * Parses the duration given in unit string {@link }
     * @param unit
     * @param amount
     * @return
     * @throws IOException
     */
    private Duration parseTemporalAmount(String unit, int amount) {
        Duration.parse()
        ChronoUnit chronoUnit = ChronoUnit.valueOf(unit);
        return chronoUnit.getDuration().multipliedBy(amount);
    }

    @Override
    public int getMinLevel() {
        return levels.stream()
                .min(Comparator.comparing(ReminderLevel::getLevel))
                .map(ReminderLevel::getLevel)
                .orElse(0);
    }

    @Override
    public int getMaxLevel() {
        return levels.stream()
                .max(Comparator.comparing(ReminderLevel::getLevel))
                .map(ReminderLevel::getLevel)
                .orElse(0);
    }

    /**
     * Returns the {@link Duration} till next revision time for the given level.
     * The delay time is specified in config.
     * @param level
     *         the review level of the card
     * @return the delay time for the given level.
     */
    @Override
    public Duration getTimeToNextRevision(int level) {
        return levels.stream()
                .filter(entry -> entry.getLevel() == level)
                .findAny()
                .map(ReminderLevel::getDelayToNextRevision)
                .orElseThrow(() -> new RuntimeException("Unable to get next reminder delay"));
    }

    /**
     * Returns the {@link Duration} between {@link Instant#now()} and next revision time
     * @param card
     *         the card
     * @return the duration till next revision time for the given card.
     */
    @Override
    public Duration getTimeToNextRevision(Card card) {
        Instant nextReminder = getNextRevision(card);
        Instant now = Instant.now();
        return Duration.between(nextReminder, now);
    }

    /**
     * Tells whether the card is ready to be reviewed, i.e whether
     * current time is after the next revision time.
     *
     * @param card
     *         the card to check
     * @return {@code true} if card is ready to be reviewed, {@code false} otherwise
     */
    @Override
    public boolean isReady(Card card) {
        Instant nextReminder = getNextRevision(card);
        return !Instant.now().isBefore(nextReminder);
    }

    /**
     * Gets the time to next revision of the given card, by computing the time to next revision
     * for the level of the card plus the last review of the card.
     *
     * @param card
     *         the card to get the next revision for
     * @return the next revision time
     */
    @Override
    public Instant getNextRevision(Card card) {
        Instant lastReview = card.getLastReview();
        int level = card.getLevel();
        TemporalAmount nextReminderDelay = this.getTimeToNextRevision(level);
        return lastReview.plus(nextReminderDelay);
    }
}
