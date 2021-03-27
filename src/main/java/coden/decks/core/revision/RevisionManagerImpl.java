package coden.decks.core.revision;

import coden.decks.core.data.Card;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
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
     * Creates a new revision manager from the given config by deserializing the config to
     * array of {@link RevisionConfigEntry} and creating correspongin {@link RevisionLevel}s
     *
     * @param is
     *         the input stream of config
     * @throws IOException
     *         if the deserializing fails
     */
    public RevisionManagerImpl(InputStream is) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RevisionConfigEntry[] entries = objectMapper.readValue(Objects.requireNonNull(is), RevisionConfigEntry[].class);

        for (RevisionConfigEntry entry : entries) {
            for (int level : entry.getLevels()) {
                addLevel(entry.getDelayToRevision(), level);
            }
        }
    }

    /**
     * Adds a new {@link RevisionLevel} from the given delay to next revision and the given level
     *
     * @param delayToNextRevision
     *         the string representing delay to next revision
     * @param level
     *         the level
     * @throws IOException
     *         if such a revision level already exists
     */
    private void addLevel(String delayToNextRevision, int level) throws IOException {
        Duration amount = Duration.parse(delayToNextRevision);
        RevisionLevel newLevel = new RevisionLevel(level, amount);
        if (levels.contains(newLevel)) throw new IOException("Found two revision level entries with the same levels");
        levels.add(newLevel);
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
}
