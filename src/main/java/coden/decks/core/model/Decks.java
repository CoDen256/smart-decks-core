package coden.decks.core.model;

import coden.decks.core.data.Card;
import coden.decks.core.data.SimpleCard;
import coden.decks.core.persistence.Database;
import coden.decks.core.revision.RevisionManager;
import coden.decks.core.user.User;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Basic implementation of {@link DecksModel}, that makes straight requests
 * to given databases without caching or any other optimization functionality
 */
public class Decks implements DecksModel {

    /** The database where cards are saved and are fetched from */
    private final Database database;
    /** The Revision manager to provide functionality to compute next revisions */
    private final RevisionManager revisioner;
    /** The current user of the decks */
    private User user;

    public Decks(User user, RevisionManager revisioner, Database database) {
        this.database = database;
        this.revisioner = revisioner;
        setUser(user);
    }

    /**
     * Creates a new card, sets the given front side and back side. The level is set to
     * {@link RevisionManager#getMinLevel()} and review is updated as well.
     *
     * @param frontSide
     *         the front side of the card
     * @param backSide
     *         the back side of the card
     * @return a new card
     */
    @Override
    public Card createCard(String frontSide, String backSide) {
        return new SimpleCard.Builder()
                .setFrontSide(frontSide)
                .setBackSide(backSide)
                .setLevel(revisioner.getMinLevel())
                .setLastReview(Instant.now())
                .create();
    }

    /**
     * Extracts the front side of the given card
     *
     * @param card
     *         the card, which front side has to be displayed
     * @return the card
     */
    @Override
    public String getFrontSide(Card card) {
        return card.getFrontSide();
    }

    /**
     * Extracts the back side of the given card
     *
     * @param card
     *         the card, which back side has to be displayed
     * @return the card
     */
    @Override
    public String getBackSide(Card card) {
        return card.getBackSide();
    }

    /**
     * Sets the given card as known. Updates the level, last review and saves the
     * card by making the request.
     *
     * @param card
     *         the card to set as known
     * @return the request
     */
    @Override
    public CompletableFuture<Void> setKnow(Card card) {
        final SimpleCard newSimpleCard = new SimpleCard.Builder(card)
                .setLevel(Math.min(revisioner.getMaxLevel(), card.getLevel() + 1))
                .setLastReview(Instant.now())
                .create();

        return database.addOrUpdateEntry(newSimpleCard);
    }

    /**
     * Sets the given card as unknown. Updates the level, last review and saves the
     * card by making the request
     *
     * @param card
     *         the card to set as unknown at the time of reviewing it
     * @return the request
     */
    @Override
    public CompletableFuture<Void> setDontKnow(Card card) {
        final SimpleCard newSimpleCard = new SimpleCard.Builder(card)
                .setLevel(Math.max(revisioner.getMinLevel(), card.getLevel() - 1))
                .setLastReview(Instant.now())
                .create();

        return database.addOrUpdateEntry(newSimpleCard);
    }

    /**
     * Adds or updates the given card.
     *
     * @param card
     *         the card to save
     * @return the request to add the card
     */
    @Override
    public CompletableFuture<Void> addCard(Card card) {
        return database.addOrUpdateEntry(card);
    }

    /**
     * Deletes the given card from the database
     *
     * @param card
     *         the card to delete
     * @return the request to delete the card
     */
    @Override
    public CompletableFuture<Void> deleteCard(Card card) {
        return database.deleteEntry(card);
    }

    /**
     * Gets the next ready card to be reviewed, i.e the next ready card or {@code null} if
     * no cards are ready.
     *
     * @return the completable future wrapping the card.
     */
    @Override
    public CompletableFuture<Card> getNextCard() {
        return getReadyCards().thenApply(cards -> cards.isEmpty() ? null : cards.get(0));
    }

    @Override
    public CompletableFuture<List<Card>> getReadyCards() {
        return database.getLessOrEqualLevel(revisioner.getMaxLevel() - 1)
                .thenApply(this::findReadyCards);
    }

    /**
     * Helper method to filter only ready cards
     *
     * @param cards
     *         the cards to filter
     * @return the list of ready cards.
     */
    private List<Card> findReadyCards(Stream<Card> cards) {
        return cards.filter(revisioner::isReady)
                .sorted(Comparator.comparing((Function<Card, Duration>) revisioner::getTimeToNextRevision).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<Card>> getPendingCards() {
        return database.getLessOrEqualLevel(revisioner.getMaxLevel() - 1)
                .thenApply(this::findPendingCards);
    }

    /**
     * Helper method to filter all pending cards
     *
     * @param cards
     *         the cards to filter
     * @return the list of pending cards
     */
    private List<Card> findPendingCards(Stream<Card> cards) {
        return cards.filter(c -> !revisioner.isReady(c))
                .sorted(Comparator.comparing((Function<Card, Duration>) revisioner::getTimeToNextRevision).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<Card>> getDoneCards() {
        return database.getGreaterOrEqualLevel(revisioner.getMaxLevel())
                .thenApply(this::collect);
    }

    @Override
    public CompletableFuture<List<Card>> getAllCards() {
        return database.getAllEntries().thenApply(this::collect);
    }

    private List<Card> collect(Stream<Card> s) {
        return s.collect(Collectors.toList());
    }

    /**
     * Updates the current user of the card and the user of the database as well.
     *
     * @param user
     *         the user to be set.
     */
    @Override
    public void setUser(User user) {
        this.user = user;
        database.setUser(user);
    }
}
