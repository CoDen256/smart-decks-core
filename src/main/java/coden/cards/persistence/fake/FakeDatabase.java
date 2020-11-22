package coden.cards.persistence.fake;

import coden.cards.data.Card;
import coden.cards.persistence.Database;
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
    public CompletableFuture<Stream<Card>> getAllEntries()  {
        final CompletableFuture<Stream<Card>> future = new CompletableFuture<>();
        future.complete(cards.stream());
        return future;
    }

    @Override
    public CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level) {
        final CompletableFuture<Stream<Card>> future = new CompletableFuture<>();
        future.complete(cards.stream().filter(card -> card.getLevel() >= level));
        return future;
    }

    @Override
    public CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level) {
        final CompletableFuture<Stream<Card>> future = new CompletableFuture<>();
        future.complete(cards.stream().filter(card -> card.getLevel() <= level));
        return future;
    }

    @Override
    public CompletableFuture<Void> deleteEntry(Card entry) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        cards.removeIf(e -> e.getFirstSide().equals(entry.getFirstSide()));
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> addOrUpdateEntry(Card entry){
        final CompletableFuture<Void> future = new CompletableFuture<>();
        deleteEntry(entry);
        cards.add(entry);
        future.complete(null);
        return future;
    }
}
