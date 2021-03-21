package coden.decks.core.data;

import java.time.Instant;

/**
 * The general interface representing a card instance.
 */
public interface Card {

    /**
     * Returns the front side of the card. This side will be usually displayed to the user.
     * The front side is unique and can be used to identify each card.
     *
     * @return the front side
     */
    String getFrontSide();

    /**
     * Returns the back side of the card. This side will be usually hidden from the user
     *
     * @return the back side
     */
    String getBackSide();

    /**
     * Returns the level of the card. The level represents the current level of progress
     * in memorizing the word/phrase on the both sides of the card.
     * Each time the user memorizes/learns the level is incremented.
     * Each time the user forgets the card the level is either decremented or stays the same.
     *
     * @return the level of progress
     */
    int getLevel();

    /**
     * Returns the last time the user saw the card. Each time the card is displayed to the user
     * the last review time is updated.
     *
     * @return the last review time
     */
    Instant getLastReview();
}
