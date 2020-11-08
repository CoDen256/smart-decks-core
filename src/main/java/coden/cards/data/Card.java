package coden.cards.data;

import java.time.Instant;

public interface Card {

    int getId();

    String getFirstSide();

    String getSecondSide();

    int getLevel();

    Instant getLastReview();
}
