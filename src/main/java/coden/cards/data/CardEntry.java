package coden.cards.data;

import java.time.Instant;
import java.util.Objects;

public class CardEntry implements Card {

    private final int id;
    private final String firstSide;
    private final String secondSide;
    private final int level;
    private final Instant lastReview;

    public CardEntry(int id, String firstSide, String secondSide, int level, Instant lastReview) {
        this.id = id;
        this.firstSide = Objects.requireNonNull(firstSide);
        this.secondSide = Objects.requireNonNull(secondSide);
        this.level = level;
        this.lastReview = Objects.requireNonNull(lastReview);
    }

    public CardEntry(CardEntry entry) {
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

    public static class Builder{

        private int id = -1;
        private String firstSide;
        private String secondSide;
        private int level = -1;
        private Instant lastReview;

        public Builder() {
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setFirstSide(String firstSide) {
            this.firstSide = firstSide;
        }

        public void setSecondSide(String secondSide) {
            this.secondSide = secondSide;
        }


        public void setLevel(int level) {
             this.level = level;
        }

        public void setLastReview(Instant lastReview) {
            this.lastReview = lastReview;
        }

        public CardEntry create(){
            return new CardEntry(id, firstSide, secondSide, level, lastReview);
        }
    }
}
