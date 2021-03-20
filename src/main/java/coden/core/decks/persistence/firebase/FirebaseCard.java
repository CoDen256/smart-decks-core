package coden.core.decks.persistence.firebase;

import coden.core.decks.data.Card;
import java.time.Instant;
import java.util.HashMap;

public class FirebaseCard implements Card {

    private String firstSide;
    private String secondSide;
    private int level;
    private HashMap<String, Long> lastReview;

    public FirebaseCard() { }

    @Override
    public String getFrontSide() {
        return firstSide;
    }

    @Override
    public String getBackSide() {
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
