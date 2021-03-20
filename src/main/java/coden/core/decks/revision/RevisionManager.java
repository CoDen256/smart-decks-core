package coden.core.decks.revision;

import coden.core.decks.data.Card;

import java.time.Duration;
import java.time.Instant;

/**
 * The represents basic revision manager. The {@code RevisionManager} can be used to manage the current
 * level of the cards, their last review time and the next required review time.
 */
public interface RevisionManager {
    /**
     * Returns the minimal possible level of memorizing progress of a card
     *
     * @return the minimal level
     */
    int getMinLevel();

    /**
     * Returns the maximal possible level of memorizing progress of a card
     *
     * @return the maximal level
     */
    int getMaxLevel();

    /**
     * Returns the {@link Duration} till the next required review time of the card of the given level.
     * For example, for level 5 the delay till the revision is bigger than for level 4(depending on config)
     *
     * @param level
     *         the review level of the card
     * @return the duration till next review time
     */
    Duration getTimeToNextRevision(int level);

    /**
     * Returns the {@link Duration} till the next revision time.
     *
     * @param card
     *         the card
     * @return the {@link Duration} representing the time to next revision
     */
    Duration getTimeToNextRevision(Card card);

    /**
     * Tells whether the given card is ready to be reviewed.
     *
     * @param card
     *         the card to check
     * @return {@code true} if card is ready to be reviewed, {@code false} otherwise
     */
    boolean isReady(Card card);

    /**
     * Returns the {@link Instant} of the next required time, when the given card
     * should be reviewed.
     *
     * @return the next time when a card should be review.
     */
    Instant getNextRevision(Card card);
}
