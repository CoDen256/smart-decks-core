package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.reminder.BaseReminder;
import coden.cards.user.User;
import coden.cards.persistence.Database;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CardModelImpl implements CardModel {

    private final List<CardObserver> observers = new LinkedList<>();

    private final Database database;
    private final BaseReminder reminder;
    private User user;

    public CardModelImpl(BaseReminder reminder, Database database) {
        this.database = database;
        this.reminder = reminder;
    }

    @Override
    public void addCard(Card card) throws Exception {
        database.addOrUpdateEntry(card);
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
    public void deleteCard(Card card) throws Exception {
        database.deleteEntry(card);
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
    public void setDontKnow(Card card) throws Exception {
        final CardEntry newCardEntry = new CardEntry.Builder(card)
                .setLevel(Math.max(reminder.getMinLevel(), card.getLevel() - 1))
                .create();

        database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public void setKnow(Card card) throws Exception {
        final CardEntry newCardEntry = new CardEntry.Builder(card)
                .setLevel(Math.min(reminder.getMaxLevel(), card.getLevel() + 1))
                .create();

        database.addOrUpdateEntry(newCardEntry);
    }

    @Override
    public List<Card> getReadyCards() throws Exception {
        return database.getLessOrEqualLevel(reminder.getMaxLevel())
                .filter(reminder::shouldRemind)
                .collect(Collectors.toList());
    }

    @Override
    public List<Card> getDoneCards() throws Exception {
        return database.getGreaterOrEqualLevel(reminder.getMaxLevel())
                .collect(Collectors.toList());
    }

    @Override
    public List<Card> getAllCards() throws Exception {
        return database.getAllEntries().collect(Collectors.toList());
    }

    @Override
    public void setUser(User user) throws Exception {
        this.user = user;
        database.setUser(user);
    }
}
