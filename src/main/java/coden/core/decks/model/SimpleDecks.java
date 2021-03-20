package coden.core.decks.model;

import coden.core.decks.persistence.Database;
import coden.core.decks.reminder.BaseReminder;
import coden.core.decks.data.Card;
import coden.core.decks.data.SimpleCard;
import coden.core.decks.user.User;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleDecks implements DecksModel {

    private final Database database;
    private final BaseReminder reminder;
    private User user;

    public SimpleDecks(User user, BaseReminder reminder, Database database) {
        this.database = database;
        this.reminder = reminder;
        setUser(user);
    }

    @Override
    public Card createCard(String firstSide, String secondSide) {
        return new SimpleCard.Builder()
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
    public CompletableFuture<Void> setKnow(Card card){
        final SimpleCard newSimpleCard = new SimpleCard.Builder(card)
                .setLevel(Math.min(reminder.getMaxLevel(), card.getLevel() + 1))
                .setLastReview(Instant.now())
                .create();

        return database.addOrUpdateEntry(newSimpleCard);
    }

    @Override
    public CompletableFuture<Void> setDontKnow(Card card) {
        final SimpleCard newSimpleCard = new SimpleCard.Builder(card)
                .setLevel(Math.max(reminder.getMinLevel(), card.getLevel() - 1))
                .setLastReview(Instant.now())
                .create();

        return database.addOrUpdateEntry(newSimpleCard);
    }

    @Override
    public CompletableFuture<Void> addCard(Card card){
        return database.addOrUpdateEntry(card);
    }

    @Override
    public CompletableFuture<Void> deleteCard(Card card){
        return database.deleteEntry(card);
    }

    @Override
    public CompletableFuture<Card> getNextCard(){
        return getPendingCards().thenApply(cards -> cards.get(0));
    }

    @Override
    public CompletableFuture<List<Card>> getReadyCards() {
        return database.getLessOrEqualLevel(reminder.getMaxLevel() - 1)
                .thenApply(this::findReadyCards);
    }

    private List<Card> findReadyCards(Stream<Card> cards){
        return cards.filter(reminder::shouldRemind)
                .sorted(Comparator.comparing(reminder::getOvertime).reversed())
                .collect(Collectors.toList());
    }
    @Override
    public CompletableFuture<List<Card>> getPendingCards() {
        return database.getLessOrEqualLevel(reminder.getMaxLevel() - 1)
                .thenApply(this::findPendingCards);
    }

    private List<Card> findPendingCards(Stream<Card> cards) {
        return cards.filter((c) -> !reminder.shouldRemind(c))
                .sorted(Comparator.comparing(reminder::getOvertime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<Card>> getDoneCards() {
        return database.getGreaterOrEqualLevel(reminder.getMaxLevel())
                .thenApply(this::collect);
    }

    @Override
    public CompletableFuture<List<Card>> getAllCards(){
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