package coden.cards.persistence;

import coden.cards.data.Card;
import coden.cards.user.User;
import java.util.stream.Stream;

public interface Database {
    void setUser(User user) throws Exception;

    Stream<Card> getAllEntries() throws Exception;
    Stream<Card> getGreaterOrEqualLevel(int level) throws Exception;

    Stream<Card> getLessOrEqualLevel(int level) throws Exception;

    void deleteEntry(Card entry) throws Exception;

    void addOrUpdateEntry(Card entry) throws Exception;
}
