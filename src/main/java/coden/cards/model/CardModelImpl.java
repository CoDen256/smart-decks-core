package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.persistence.Database;
import coden.cards.reminder.Reminder;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CardModelImpl implements CardModel {

    private final List<CardObserver> observers = new LinkedList<>();

    private final Database database;
    private final Reminder reminder;
    private final Predicate<Card> unknownCardFilter;

    public CardModelImpl(Reminder reminder, Predicate<Card> unknownCardFilter, Database database) {
        this.database = database;
        this.reminder = reminder;
        this.unknownCardFilter = unknownCardFilter;
    }

    @Override
    public Card addCard(Card card) {
        database.addOrUpdateEntry(card);
        return card;
    }

    @Override
    public Card createCard(String firstSide, String secondSide) {
        return new CardEntry.Builder()
                .setFirstSide(firstSide)
                .setSecondSide(secondSide)
                .setLevel(0)
                .setLastReview(Instant.now())
                .create();
    }

    @Override
    public void deleteCard(Card card) {
        database.deleteEntry(card.getFirstSide());
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
                .setLevel(Math.max(reminder.getMinLevel(), card.getLevel() - 1))
                .create();

        database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public void setKnow(Card card) {
        final CardEntry newCardEntry = new CardEntry.Builder(card)
                .setLevel(Math.min(reminder.getMaxLevel(), card.getLevel() + 1))
                .create();

        database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public List<Card> getCardsToLearn() {
        return database.getLessOrEqualLevel(reminder.getMaxLevel())
                .filter(unknownCardFilter)
                .collect(Collectors.toList());
    }

    @Override
    public List<Card> getLearnedCards() {
        return database.getGreaterOrEqualLevel(reminder.getMaxLevel()+1)
                .collect(Collectors.toList());
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
