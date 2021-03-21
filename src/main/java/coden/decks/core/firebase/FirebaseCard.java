package coden.decks.core.firebase;

import coden.decks.core.data.Card;
import coden.decks.core.data.SimpleCard;

import java.time.Instant;
import java.util.Map;

/**
 * The {@code FirebaseCard} represents a firebase mapping of the {@link Card},
 * that will be used by Firebase to store the cards. The {@code FirebaseCard} is designed to
 * be used exclusively for deserialization from json. To create a {@link Card} from scratch
 * or any other card use {@link SimpleCard} instead.
 *
 * @see SimpleCard
 */
public class FirebaseCard implements Card {

    /** The front side field mapping */
    private String frontSide;
    /** The back side field mapping */
    private String backSide;
    /** The level of the card mapping */
    private int level;

    /**
     * The the stored in firebase mapping of {@link Instant}.
     * The map contains fields like 'epochSecond' and 'nano' that can be converted to {@link Instant}
     */
    private Map<String, Long> lastReview;

    /** A private constructor for deserialization */
    FirebaseCard() {
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
        return Instant.ofEpochSecond(lastReview.get("epochSecond"), lastReview.get("nano"));
    }

    @Override
    public String toString() {
        return String.format("Card<%s:%s>", frontSide, backSide);
    }
}
