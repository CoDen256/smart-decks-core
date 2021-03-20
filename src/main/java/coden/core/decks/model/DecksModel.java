package coden.core.decks.model;

import coden.core.decks.data.Card;
import coden.core.decks.user.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DecksModel {
    Card createCard(String firstSide, String secondSide);
    String showFirstSide(Card card);
    String showSecondSide(Card card);

    CompletableFuture<Void> setKnow(Card card);
    CompletableFuture<Void> setDontKnow(Card card);

    CompletableFuture<Void> addCard(Card card);

    CompletableFuture<Void> deleteCard(Card card);

    CompletableFuture<Card> getNextCard();

    CompletableFuture<List<Card>> getReadyCards();
    CompletableFuture<List<Card>> getPendingCards();
    CompletableFuture<List<Card>> getDoneCards();
    CompletableFuture<List<Card>> getAllCards();

    void setUser(User user);

}
