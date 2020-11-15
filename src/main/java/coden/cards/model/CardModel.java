package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.user.User;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CardModel {
    Card createCard(String firstSide, String secondSide) throws Exception;
    String showFirstSide(Card card);
    String showSecondSide(Card card);

    CompletableFuture<Void> setKnow(Card card) throws Exception;
    CompletableFuture<Void> setDontKnow(Card card) throws Exception;

    CompletableFuture<Void> addCard(Card card) throws Exception;

    CompletableFuture<Void> deleteCard(Card card) throws Exception;

    CompletableFuture<Card> getNextCard() throws Exception;

    CompletableFuture<List<Card>> getReadyCards() throws Exception;
    CompletableFuture<List<Card>> getPendingCards() throws Exception;
    CompletableFuture<List<Card>> getDoneCards() throws Exception;
    CompletableFuture<List<Card>> getAllCards() throws Exception;

    void setUser(User user) throws Exception;

}
