package coden.core.decks.persistence;

import coden.core.decks.data.Card;
import coden.core.decks.user.User;

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
