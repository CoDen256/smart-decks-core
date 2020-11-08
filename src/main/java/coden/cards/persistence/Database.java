package coden.cards.persistence;

import coden.cards.data.Card;
import java.time.Instant;
import java.util.stream.Stream;

public interface Database {
    Stream<Card> getAllEntries();
    Stream<Card> getUnknownEntries();
    Stream<Card> getKnownEntries();
    Stream<Card> getOlderThan(Instant base);
    Stream<Card> getYoungerThan(Instant base);
    Stream<Card> getGreaterOrEqualLevel(int level);
    Stream<Card> getLessOrEqualLevel(int level);

    Card getCardEntry(int id);

    void deleteEntry(int id);

    void addOrUpdateEntry(Card entry);
}
