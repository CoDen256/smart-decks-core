package coden.cards.data;

import java.time.Instant;
import java.util.Objects;

public class CardEntry implements Card {

    private String firstSide;
    private String secondSide;
    private int level;
    private Instant lastReview;

    CardEntry(String firstSide, String secondSide, int level, Instant lastReview) {
        this.firstSide = firstSide;
        this.secondSide = secondSide;
        this.level = level;
        this.lastReview = lastReview;
    }

    public CardEntry(CardEntry entry) {
        this.firstSide = entry.getFirstSide();
        this.secondSide = entry.getSecondSide();
        this.level = entry.getLevel();
        this.lastReview = entry.getLastReview();
    }

    /** No arg constructor for deserialization */
    CardEntry() { }

    @Override
    public String getFirstSide() {
        return firstSide;
    }

    @Override
    public String getSecondSide() {
        return secondSide;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Instant getLastReview() {
        return lastReview;
    }

    public static class Builder {

        private String firstSide;
        private String secondSide;
        private int level = -1;
        private Instant lastReview;

        public Builder() {
        }

        public Builder(Card cardEntry) {
            setFirstSide(cardEntry.getFirstSide());
            setSecondSide(cardEntry.getSecondSide());
            setLastReview(cardEntry.getLastReview());
            setLevel(cardEntry.getLevel());
        }


        public Builder setFirstSide(String firstSide) {
            this.firstSide = firstSide;
            return this;
        }

        public Builder setSecondSide(String secondSide) {
            this.secondSide = secondSide;
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

        public CardEntry create() {
            validate();
            return new CardEntry(
                    Objects.requireNonNull(firstSide),
                    Objects.requireNonNull(secondSide),
                    level,
                    Objects.requireNonNull(lastReview));
        }

        private void validate() {
            if (this.level == -1) {
                throw new IllegalStateException("Level has to be specified");
            }
        }
    }
}
