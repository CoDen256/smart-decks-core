package coden.decks.core.revision;

import coden.decks.core.data.Card;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * The actual implementation of {@link RevisionManager} that reads
 * from the given config file, which contains the revision levels and their corresponding
 * delays.
 *
 * @see RevisionLevel
 */
public class RevisionManagerImpl implements RevisionManager {
    /** The revision levels */
    private final Set<RevisionLevel> levels = new HashSet<>();

    /**
     * Creates a new revision manager from the given revision levels. Revision Manager
     * operates on this levels to compute time to next revision.
     *
     * @param levels
     *         the revision levels to operate on
     */
    public RevisionManagerImpl(Collection<RevisionLevel> levels){
        setRevisionLevels(levels);
    }

    @Override
    public int getMinLevel() {
        return levels.stream()
                .min(Comparator.comparing(RevisionLevel::getLevel))
                .map(RevisionLevel::getLevel)
                .orElse(0);
    }

    @Override
    public int getMaxLevel() {
        return levels.stream()
                .max(Comparator.comparing(RevisionLevel::getLevel))
                .map(RevisionLevel::getLevel)
                .map(i -> i + 1)
                .orElse(0);
    }

    /**
     * Returns the {@link Duration} till next revision time for the given level.
     * The delay time is specified in config.
     *
     * @param level
     *         the review level of the card
     * @return the delay time for the given level.
     */
    @Override
    public Duration getTimeToNextRevision(int level) {
        return levels.stream()
                .filter(entry -> entry.getLevel() == level)
                .findAny()
                .map(RevisionLevel::getDelayToNextRevision)
                .orElseThrow(() -> new RuntimeException("Unable to get next reminder delay"));
    }

    /**
     * Returns the {@link Duration} between {@link Instant#now()} and next revision time
     *
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

    @Override
    public void setRevisionLevels(Collection<RevisionLevel> levels) {
        this.levels.clear();
        this.levels.addAll(levels);
    }
}
