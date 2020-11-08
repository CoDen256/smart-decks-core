package coden.cards.model;

import coden.cards.data.Card;
import java.util.List;

public interface CardModel {
    Card addCard(Card card);
    Card createCard(String firstSide, String secondSide);

    void deleteCard(Card card);

    String showFirstSide(Card card);
    String showSecondSide(Card card);

    void setDontKnow(Card card);
    void setKnow(Card card);

    List<Card> getCardsToLearn();
    List<Card> getLearnedCards();

    void registerObserver(CardObserver cardObserver);
    void removeObserver(CardObserver cardObserver);

}
