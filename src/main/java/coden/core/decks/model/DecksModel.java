package coden.core.decks.model;

import coden.core.decks.data.Card;
import coden.core.decks.reminder.Reminder;
import coden.core.decks.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a decks model. Via {@link DecksModel} it is possible to manage cards in any possible way.
 */
public interface DecksModel {

    /**
     * Creates a completely new card(without saving it: {@link DecksModel#addCard}).
     * The new card has the lowest level of progress, and the last review corresponds the current
     * time.
     *
     * @param frontSide
     *         the front side of the card
     * @param backSide
     *         the back side of the card
     * @return a new {@link Card}
     */
    Card createCard(String frontSide, String backSide);

    /**
     * Gets the front side of the given card. No requests and changes to the card are made.
     *
     * @param card
     *         the card, which front side has to be displayed
     * @return the front side of the given card
     */
    String getFrontSide(Card card);

    /**
     * Gets the back side of the given card. No requests and changes to the card are made.
     *
     * @param card
     *         the card, which back side has to be displayed
     * @return the back side of the given card
     */
    String getBackSide(Card card);

    /**
     * Makes an asynchronous request to the server and sets the given card as known.
     * It means that the user at the time of reviewing it CAN recall the back side of the card / the meaning of the card.
     *
     * Makes a request to the server to update the card:
     *  * increases the current level of memorizing by 1.
     *  * updates the review time
     *
     * @param card
     *         the card to set as known
     * @return the {@link CompletableFuture} representing the call to the server of updating the card
     */
    CompletableFuture<Void> setKnow(Card card);

    /**
     * Makes an asynchronous request to the server and sets the given card as unknown.
     * It means that the user at the time of reviewing it CANNOT recall the back side of the card / the meaning of the card.
     *
     * Makes a request to the server to update the card:
     *  * decreases the current level of memorizing by 1.
     *  * updates the review time
     *
     * @param card
     *         the card to set as unknown at the time of reviewing it
     * @return the {@link CompletableFuture} representing the call to the server of updating the card
     */
    CompletableFuture<Void> setDontKnow(Card card);

    /**
     * Adds (or saves) the given card. Makes an asynchronous request to the server to add it.
     * If the card with the same front side already exists, the card will be updated.
     * @param card the card to save
     * @return the request made to the server
     */
    CompletableFuture<Void> addCard(Card card);

    /**
     * Deletes the given card. Makes an asynchronous request to the server. 
     * As unique identifier the front side of the card is used.
     * @see Card#getFrontSide()
     * @param card the card to delete
     * @return the request made to the server
     */
    CompletableFuture<Void> deleteCard(Card card);

    /**
     * Returns the next card to be learned. Next card has the biggest deadline of review, i.e
     * it should be the first one to be reviewed.
     * @see Reminder#getTimeToNextRevision
     * @return the request obtaining the next card to review
     */
    CompletableFuture<Card> getNextCard();

    /**
     * Makes an asynchronous request to get all cards that are ready to be learned, i.e
     * the cards have to be reviewed
     * @return the request to obtain the list of cards
     */
    CompletableFuture<List<Card>> getReadyCards();

    /**
     * Makes an asynchronous request to get all pending cards, i.e the ones that are not ready
     * to be learned
     * @return the request to obtain the list of pending cards
     */
    CompletableFuture<List<Card>> getPendingCards();

    /**
     * Makes an asynchronous request to get all learned cards, i.e the cards with max level
     * @return the request to obtain the list of learned cards.
     */
    CompletableFuture<List<Card>> getDoneCards();

    /**
     * Makes an asynchronous request to get all the cards.
     * @return the request to obtain the list of all cards.
     */
    CompletableFuture<List<Card>> getAllCards();

    /**
     * Sets the current user of the model
     * @param user the user to be set.
     */
    void setUser(User user);
}
