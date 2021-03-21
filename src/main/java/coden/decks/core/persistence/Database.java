package coden.decks.core.persistence;

import coden.decks.core.data.Card;
import coden.decks.core.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Represents a database interface, that can be used to perform different
 * operations on {@link Card}s: fetch, add, update, delete etc.
 */
public interface Database {
    /**
     * Sets a given user for the current database connection
     *
     * @param user
     *         the user
     */
    void setUser(User user);

    /**
     * Returns all entries asynchronously.
     *
     * @return the request to get stream of all cards.
     */
    CompletableFuture<Stream<Card>> getAllEntries();

    /**
     * Returns all entries of cards, that have level greater than given asynchronously.
     *
     * @param level
     *         the given level
     * @return the request to get stream of all cards, with level greater or equal than given level.
     */
    CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level);

    /**
     * Returns all entries of cards, that have level less than given asynchronously.
     *
     * @param level
     *         the given level
     * @return the request to get stream of all cards, with level less or equal than given level.
     */
    CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level);

    /**
     * Creates an asynchronous request to delete a given card.
     *
     * @param card
     *         the card to be deleted
     * @return the request to delete the given card
     */
    CompletableFuture<Void> deleteEntry(Card card);

    /**
     * Creates an asynchronous request to add or update a given card.
     * As id the {@link Card#getFrontSide()} is used
     *
     * @param card
     *         the card to be added
     * @return the request to add/update the card
     */
    CompletableFuture<Void> addOrUpdateEntry(Card card);
}
