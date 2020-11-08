package coden.cards.model;

import coden.cards.data.Card;
import java.util.stream.Stream;

public interface CardObserver {
    void notify(Stream<Card> newCards);
}
