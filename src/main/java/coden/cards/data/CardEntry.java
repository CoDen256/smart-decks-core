package coden.cards.data;

import java.time.Instant;
import java.util.Objects;

public class CardEntry implements Card {

    private final int id;
    private final String firstSide;
    private final String secondSide;
    private final int level;
    private final Instant lastReview;

    CardEntry(int id, String firstSide, String secondSide, int level, Instant lastReview) {
        this.id = id;
        this.firstSide = Objects.requireNonNull(firstSide);
        this.secondSide = Objects.requireNonNull(secondSide);
        this.level = level;
        this.lastReview = Objects.requireNonNull(lastReview);
    }

    CardEntry(CardEntry entry) {
        this.id = entry.getId();
        this.firstSide = entry.getFirstSide();
        this.secondSide = entry.getSecondSide();
        this.level = entry.level;
        this.lastReview = entry.lastReview;
    }

    @Override
    public int getId() {
        return id;
    }

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

        private int id = -1;
        private String firstSide;
        private String secondSide;
        private int level = -1;
        private Instant lastReview;

        public Builder() {
        }

        public Builder(Card cardEntry) {
            setId(cardEntry.getId());
            setFirstSide(cardEntry.getFirstSide());
            setSecondSide(cardEntry.getSecondSide());
            setLastReview(cardEntry.getLastReview());
            setLevel(cardEntry.getLevel());
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
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
            return new CardEntry(id, firstSide, secondSide, level, lastReview);
        }
    }
}
