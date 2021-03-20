package coden.core.decks.data;

import java.time.Instant;

public interface Card {

    String getFirstSide();

    String getSecondSide();

    int getLevel();

    Instant getLastReview();
}
