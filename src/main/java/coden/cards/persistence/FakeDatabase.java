package coden.cards.persistence;

import coden.cards.data.Card;
import coden.cards.user.User;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class FakeDatabase implements Database {

    private final Set<Card> cards = new HashSet<>();

    public FakeDatabase() {

    }

    @Override
    public void setUser(User user) {

    }

    @Override
    public CompletableFuture<Stream<Card>> getAllEntries() throws Exception {
        final CompletableFuture<Stream<Card>> future = new CompletableFuture<>();
        future.complete(cards.stream());
        return future;
    }

    @Override
    public CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level) throws Exception {
        final CompletableFuture<Stream<Card>> future = new CompletableFuture<>();
        future.complete(cards.stream().filter(card -> card.getLevel() >= level));
        return future;
    }

    @Override
    public CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level) throws Exception {
        final CompletableFuture<Stream<Card>> future = new CompletableFuture<>();
        future.complete(cards.stream().filter(card -> card.getLevel() <= level));
        return future;
    }

    @Override
    public CompletableFuture<Void> deleteEntry(Card entry) throws Exception {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        cards.removeIf(e -> e.getFirstSide().equals(entry.getFirstSide()));
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> addOrUpdateEntry(Card entry) throws Exception {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        deleteEntry(entry);
        cards.add(entry);
        future.complete(null);
        return future;
    }
}
