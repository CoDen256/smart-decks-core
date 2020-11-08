package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.persistence.Database;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CardModelImpl implements CardModel {

    private final List<CardObserver> observers = new LinkedList<>();

    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 10;
    private final Database database;

    public CardModelImpl(Database database) {
        this.database = database;
    }

    @Override
    public Card addCard(Card card) {
        database.addOrUpdateEntry(card);
        return card;
    }

    @Override
    public Card createCard(String firstSide, String secondSide) {
        return new CardEntry.Builder()
                .setId(Objects.hash(firstSide))
                .setFirstSide(firstSide)
                .setSecondSide(secondSide)
                .setLevel(0)
                .setLastReview(Instant.now())
                .create();
    }

    @Override
    public void deleteCard(Card card) {
        database.deleteEntry(card.getId());
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
    public void setDontKnow(Card card) {
        final CardEntry newCardEntry = new CardEntry.Builder(card)
                .setLevel(Math.max(MIN_LEVEL, card.getLevel() - 1))
                .create();

        database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public void setKnow(Card card) {
        final CardEntry newCardEntry = new CardEntry.Builder(card)
                .setLevel(Math.min(MAX_LEVEL, card.getLevel() + 1))
                .create();

        database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public List<Card> getCardsToLearn() {
        return database.getUnknownEntries()

                .collect(Collectors.toList());
    }

    @Override
    public List<Card> getLearnedCards() {
        return database.getKnownEntries().collect(Collectors.toList());
    }

    @Override
    public void registerObserver(CardObserver cardObserver) {
        observers.add(cardObserver);
    }

    @Override
    public void removeObserver(CardObserver cardObserver) {
        observers.remove(cardObserver);
    }
}
