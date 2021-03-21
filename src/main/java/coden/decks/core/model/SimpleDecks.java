package coden.decks.core.model;

import coden.decks.core.persistence.Database;
import coden.decks.core.revision.RevisionManager;
import coden.decks.core.data.Card;
import coden.decks.core.data.SimpleCard;
import coden.decks.core.user.User;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleDecks implements DecksModel {

    private final Database database;
    private final RevisionManager reminder;
    private User user;

    public SimpleDecks(User user, RevisionManager reminder, Database database) {
        this.database = database;
        this.reminder = reminder;
        setUser(user);
    }

    @Override
    public Card createCard(String frontSide, String backSide) {
        return new SimpleCard.Builder()
                .setFrontSide(frontSide)
                .setBackSide(backSide)
                .setLevel(0)
                .setLastReview(Instant.now())
                .create();
    }

    @Override
    public String getFrontSide(Card card) {
        return card.getFirstSide();
    }

    @Override
    public String getBackSide(Card card) {
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
        return getReadyCards().thenApply(cards -> cards.isEmpty() ? null : cards.get(0));
    }

    @Override
    public CompletableFuture<List<Card>> getReadyCards() {
        return database.getLessOrEqualLevel(reminder.getMaxLevel() - 1)
                .thenApply(this::findReadyCards);
    }

    private List<Card> findReadyCards(Stream<Card> cards){
        return cards.filter(reminder::isReady)
                .sorted(Comparator.comparing((Function<Card, Duration>) reminder::getTimeToNextRevision).reversed())
                .collect(Collectors.toList());
    }
    @Override
    public CompletableFuture<List<Card>> getPendingCards() {
        return database.getLessOrEqualLevel(reminder.getMaxLevel() - 1)
                .thenApply(this::findPendingCards);
    }

    private List<Card> findPendingCards(Stream<Card> cards) {
        return cards.filter(c -> !reminder.isReady(c))
                .sorted(Comparator.comparing((Function<Card, Duration>) reminder::getTimeToNextRevision).reversed())
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
