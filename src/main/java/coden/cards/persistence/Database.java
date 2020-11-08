package coden.cards.persistence;

import coden.cards.data.Card;
import java.time.Instant;
import java.util.stream.Stream;

public interface Database {
    Stream<Card> getAllEntries();

    Stream<Card> getOlderReviewThan(Instant base);
    Stream<Card> getYoungerReviewThan(Instant base);
    Stream<Card> getGreaterOrEqualLevel(int level);
    Stream<Card> getLessOrEqualLevel(int level);

    void deleteEntry(String firstSide);

    void addOrUpdateEntry(Card entry);
}
