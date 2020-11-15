package coden.cards.persistence;

import coden.cards.data.Card;
import coden.cards.user.User;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface Database {
    void setUser(User user);

    CompletableFuture<Stream<Card>> getAllEntries() throws Exception;
    CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level) throws Exception;
    CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level) throws Exception;

    CompletableFuture<Void> deleteEntry(Card entry) throws Exception;

    CompletableFuture<Void> addOrUpdateEntry(Card entry) throws Exception;
}
