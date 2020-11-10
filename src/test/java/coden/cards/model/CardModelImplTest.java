package coden.cards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.persistence.firebase.Firebase;
import coden.cards.reminder.Reminder;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CardModelImplTest {


    @Test
    void testAddAndGet() throws Exception {
        final Reminder reminder = new Reminder(read("/reminder_test.json"));
        final ReadyCardFilter cardFilter = new ReadyCardFilter(reminder);
        final Firebase database = new Firebase(UUID.randomUUID().toString(),
                read("/serviceAccountTest.json"),
                read("/firebase_test.cfg"));

        final CardModelImpl cardModel = new CardModelImpl(reminder, cardFilter, database);

        final Card card = cardModel.createCard(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        cardModel.addCard(card);
        final List<Card> cards = cardModel.getAllCards();
        final Card actual = cards.get(0);
        assertEquals(1, cards.size());
        assertEquals(card.getFirstSide(), actual.getFirstSide());
        assertEquals(card.getSecondSide(), actual.getSecondSide());
        assertEquals(card.getLevel(), actual.getLevel());
        assertEquals(card.getLastReview(), actual.getLastReview());
    }

    @Test
    void testDeleteEntry() throws Exception {
        final Reminder reminder = new Reminder(read("/reminder_test.json"));
        final ReadyCardFilter cardFilter = new ReadyCardFilter(reminder);
        final Firebase database = new Firebase(UUID.randomUUID().toString(),
                read("/serviceAccountTest.json"),
                read("/firebase_test.cfg"));

        final CardModelImpl cardModel = new CardModelImpl(reminder, cardFilter, database);

        final Card card = cardModel.createCard(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        cardModel.addCard(card);
        cardModel.deleteCard(card);
        final List<Card> cards = cardModel.getAllCards();
        assertEquals(0, cards.size());
    }

    @Test
    void testComplexQueries() throws Exception {
        final Reminder reminder = new Reminder(read("/reminder_test.json"));
        final ReadyCardFilter cardFilter = new ReadyCardFilter(reminder);
        final Firebase database = new Firebase("coden",
                read("/serviceAccountTest.json"),
                read("/firebase_test.cfg"));

        final CardModelImpl cardModel = new CardModelImpl(reminder, cardFilter, database);
        final Card card = new CardEntry.Builder()
                .setFirstSide("einsehen")
                .setSecondSide("понять, изучить, убедиться")
                .setLevel(reminder.getMaxLevel())
                .setLastReview(Instant.now())
                .create();

        cardModel.addCard(card);
        cardModel.setKnow(card);

        final List<Card> learnedCards = cardModel.getDoneCards();
        assertEquals(1, learnedCards.size());
    }

    @Test
    void testFlow() throws Exception {
        final Reminder reminder = new Reminder(read("/reminder_test.json"));
        final ReadyCardFilter cardFilter = new ReadyCardFilter(reminder);
        final Firebase database = new Firebase("coden",
                read("/serviceAccountTest.json"),
                read("/firebase_test.cfg"));

        final CardModelImpl cardModel = new CardModelImpl(reminder, cardFilter, database);
        final Card card = new CardEntry.Builder()
                .setFirstSide("einsehen")
                .setSecondSide("понять, изучить, убедиться")
                .setLevel(reminder.getMaxLevel())
                .setLastReview(Instant.now())
                .create();

        cardModel.addCard(card);
        cardModel.setKnow(card);

        final List<Card> learnedCards = cardModel.getDoneCards();
        assertEquals(1, learnedCards.size());
    }

    private InputStream read(String path){
        return CardModelImpl.class.getResourceAsStream(path);
    }
}