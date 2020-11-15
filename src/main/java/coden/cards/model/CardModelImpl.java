package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.persistence.Database;
import coden.cards.reminder.BaseReminder;
import coden.cards.user.User;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CardModelImpl implements CardModel {

    private final Database database;
    private final BaseReminder reminder;
    private User user;

    public CardModelImpl(User user, BaseReminder reminder, Database database) {
        this.database = database;
        this.reminder = reminder;
        setUser(user);
    }

    @Override
    public Card createCard(String firstSide, String secondSide) {
        return new CardEntry.Builder()
                .setFirstSide(firstSide)
                .setSecondSide(secondSide)
                .setLevel(0)
                .setLastReview(Instant.now())
                .create();
    }

    @Override
    public String showFirstSide(Card card) {
        return card.getFirstSide();
    }

    @Override
    public String showSecondSide(Card card) {
        return card.getSecondSide();
    }

    @Override
    public CompletableFuture<Void> setKnow(Card card) throws Exception {
        final CardEntry newCardEntry = new CardEntry.Builder(card)
                .setLevel(Math.min(reminder.getMaxLevel(), card.getLevel() + 1))
                .setLastReview(Instant.now())
                .create();

        return database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public CompletableFuture<Void> setDontKnow(Card card) throws Exception {
        final CardEntry newCardEntry = new CardEntry.Builder(card)
                .setLevel(Math.max(reminder.getMinLevel(), card.getLevel() - 1))
                .setLastReview(Instant.now())
                .create();

        return database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public CompletableFuture<Void> addCard(Card card) throws Exception {
        return database.addOrUpdateEntry(card);
    }

    @Override
    public CompletableFuture<Void> deleteCard(Card card) throws Exception {
        return database.deleteEntry(card);
    }

    @Override
    public CompletableFuture<Card> getNextCard() throws Exception {
        return getPendingCards().thenApply(cards -> cards.get(0));
    }

    @Override
    public CompletableFuture<List<Card>> getReadyCards() throws Exception {
        return database.getLessOrEqualLevel(reminder.getMaxLevel() - 1)
                .thenApply(this::findReadyCards);
    }

    private List<Card> findReadyCards(Stream<Card> cards){
        return cards.filter(reminder::shouldRemind)
                .sorted(Comparator.comparing(reminder::getOvertime).reversed())
                .collect(Collectors.toList());
    }
    @Override
    public CompletableFuture<List<Card>> getPendingCards() throws Exception {
        return database.getLessOrEqualLevel(reminder.getMaxLevel() - 1)
                .thenApply(this::findPendingCards);
    }

    private List<Card> findPendingCards(Stream<Card> cards) {
        return cards.filter(Predicate.not(reminder::shouldRemind))
                .sorted(Comparator.comparing(reminder::getOvertime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<Card>> getDoneCards() throws Exception {
        return database.getGreaterOrEqualLevel(reminder.getMaxLevel())
                .thenApply(this::collect);
    }

    @Override
    public CompletableFuture<List<Card>> getAllCards() throws Exception {
        return database.getAllEntries().thenApply(this::collect);
    }

    private List<Card> collect(Stream<Card> s) {
        return s.collect(Collectors.toList());
    }

    @Override
    public void setUser(User user){
        this.user = user;
        database.setUser(user);
    }
}
