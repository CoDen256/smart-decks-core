package coden.cards.persistence.firebase;

import coden.cards.data.Card;
import java.time.Instant;
import java.util.HashMap;

public class FirebaseCardEntry implements Card {

    private String firstSide;
    private String secondSide;
    private int level;
    private HashMap<String, Long> lastReview;

    public FirebaseCardEntry() { }

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
        return Instant.ofEpochSecond(lastReview.get("epochSecond"), lastReview.get("nano"));
    }
    @Override
    public String toString() {
        return "Card:"+firstSide;
    }
}
