package coden.decks.core.data;

import javax.annotation.concurrent.ThreadSafe;

import java.time.Instant;
import java.util.Objects;

/**
 * Basic implementation of the {@link Card}. The {@code SimpleCard} is immutable,
 * created only from {@link Builder} and thus thread safe
 */
@ThreadSafe
public class SimpleCard implements Card {

    /** The front side of the card */
    private String frontSide;
    /** The back side of the card */
    private String backSide;
    /** The current level of memorizing the card */
    private int level;
    /** The last review of the card */
    private Instant lastReview;

    /**
     * Creates a new {@code Simple} card from the given front and back side, level of progress and
     * last review
     *
     * @param frontSide
     *         the front side
     * @param backSide
     *         the back side
     * @param level
     *         the level of progress of the card.
     * @param lastReview
     *         the last review of the card
     */
    SimpleCard(String frontSide, String backSide, int level, Instant lastReview) {
        this.frontSide = frontSide;
        this.backSide = backSide;
        this.level = level;
        this.lastReview = lastReview;
    }

    /**
     * Represents a copy constructor. Creates a new {@code SimpleCard} from the given card.
     *
     * @param card
     *         the original card
     */
    public SimpleCard(Card card) {
        this.frontSide = card.getFrontSide();
        this.backSide = card.getBackSide();
        this.level = card.getLevel();
        this.lastReview = card.getLastReview();
    }

    /** No arg constructor for deserialization */
    SimpleCard() {
    }

    @Override
    public String getFrontSide() {
        return frontSide;
    }

    @Override
    public String getBackSide() {
        return backSide;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Instant getLastReview() {
        return lastReview;
    }

    /**
     * Represents a Builder for the {@code SimpleCard} to create a new card
     * from the given card or from the scratch.
     */
    public static class Builder {
        private String frontSide;
        private String backSide;
        private int level = -1;
        private Instant lastReview;

        /**
         * Creates a new builder with empty card
         */
        public Builder() {
        }

        /**
         * Creates a new builder for card from the given card
         * @param card the original card
         */
        public Builder(Card card) {
            setFrontSide(card.getFrontSide());
            setBackSide(card.getBackSide());
            setLastReview(card.getLastReview());
            setLevel(card.getLevel());
        }


        public Builder setFrontSide(String frontSide) {
            this.frontSide = frontSide;
            return this;
        }

        public Builder setBackSide(String backSide) {
            this.backSide = backSide;
            return this;
        }


        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setLastReview(Instant lastReview) {
            this.lastReview = lastReview;
            return this;
        }

        /**
         * Creates a {@link SimpleCard}
         * @return a new {@link SimpleCard}
         */
        public SimpleCard create() {
            validate();
            return new SimpleCard(
                    Objects.requireNonNull(frontSide),
                    Objects.requireNonNull(backSide),
                    level,
                    Objects.requireNonNull(lastReview));
        }

        private void validate() {
            if (this.level == -1) {
                throw new IllegalStateException("Level has to be specified");
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Card<%s:%s>", frontSide, backSide);
    }
}
