package coden.cards.model;

import coden.cards.data.Card;
import java.util.List;

public interface CardModel {
    Card addCard(Card card) throws Exception;
    Card createCard(String firstSide, String secondSide) throws Exception;

    void deleteCard(Card card) throws Exception;

    String showFirstSide(Card card);
    String showSecondSide(Card card);

    void setDontKnow(Card card) throws Exception;
    void setKnow(Card card) throws Exception;

    List<Card> getReadyCards() throws Exception;
    List<Card> getDoneCards() throws Exception;
    List<Card> getAllCards() throws Exception;

}
