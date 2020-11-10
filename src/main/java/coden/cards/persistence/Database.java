package coden.cards.persistence;

import coden.cards.data.Card;
import java.util.stream.Stream;

public interface Database {
    Stream<Card> getAllEntries() throws Exception;

    Stream<Card> getGreaterOrEqualLevel(int level) throws Exception;
    Stream<Card> getLessOrEqualLevel(int level) throws Exception;

    void deleteEntry(Card entry) throws Exception;

    void addOrUpdateEntry(Card entry) throws Exception;
}
