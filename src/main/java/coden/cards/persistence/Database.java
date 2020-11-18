package coden.cards.persistence;

import coden.cards.data.Card;
import coden.cards.user.User;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface Database {
    void setUser(User user);

    CompletableFuture<Stream<Card>> getAllEntries();
    CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level);
    CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level);

    CompletableFuture<Void> deleteEntry(Card entry);

    CompletableFuture<Void> addOrUpdateEntry(Card entry);
}
