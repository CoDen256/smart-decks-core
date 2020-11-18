package coden.cards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.persistence.Database;
import coden.cards.persistence.FakeDatabase;
import coden.cards.user.User;
import coden.cards.user.UserEntry;
import coden.cards.persistence.firebase.Firebase;
import coden.cards.reminder.Reminder;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.xml.crypto.Data;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//@Disabled
class CardModelImplTest {


    final Reminder reminder = new Reminder(read("/reminder_test.json"));
    final User user = new UserEntry("coden");
    final Firebase firebase = new Firebase(
            read("/serviceAccountTest.json"),
            read("/firebase_test.cfg"));


    CardModelImplTest() throws IOException { }

//    @Test
    void testAddAndGet() throws Exception {
        final Firebase database = new Firebase(
                read("/serviceAccountTest.json"),
                read("/firebase_test.cfg"));

        final CardModelImpl cardModel = new CardModelImpl(getRandomUser(), reminder, database);

        final Card card = cardModel.createCard(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        cardModel.addCard(card);
//        final List<Card> cards = cardModel.getAllCards();
//        final Card actual = cards.get(0);
//        assertEquals(1, cards.size());
//        assertEquals(card.getFirstSide(), actual.getFirstSide());
//        assertEquals(card.getSecondSide(), actual.getSecondSide());
//        assertEquals(card.getLevel(), actual.getLevel());
//        assertEquals(card.getLastReview(), actual.getLastReview());
    }

    private UserEntry getRandomUser() {
        return new UserEntry(UUID.randomUUID().toString());
    }

//    @Test
    void testDeleteEntry() throws Exception {
        final CardModelImpl cardModel = new CardModelImpl(user, reminder, firebase);
        final Card card = cardModel.createCard(UUID.randomUUID().toString(), UUID.randomUUID().toString());
//        cardModel.addCard(card);
//        cardModel.deleteCard(card);
//        final List<Card> cards = cardModel.getAllCards();
//        assertEquals(0, cards.size());
    }

    @Test
    void testComplexQueries() throws Exception {
        final CardModelImpl cardModel = new CardModelImpl(user, reminder, firebase);

        final Card card = new CardEntry.Builder()
                .setFirstSide("einsehen")
                .setSecondSide("понять, изучить, убедиться")
                .setLevel(reminder.getMaxLevel())
                .setLastReview(Instant.now())
                .create();

        cardModel.addCard(card);
        cardModel.setKnow(card);
//
//        final List<Card> learnedCards = cardModel.getDoneCards();
//        assertEquals(1, learnedCards.size());
    }

//    @Test
    void testFlow() throws Exception {
        final Reminder reminder = new Reminder(read("/reminder_test.json"));
        final Database database = new FakeDatabase();
        final CardModel cardModel = new CachedCardModel(new UserEntry("balbes"), reminder, firebase, 1);
        database.addOrUpdateEntry(cardModel.createCard("nahen", "приближаться"));
        database.addOrUpdateEntry(cardModel.createCard("Wege einschlagen", "выбирать пути"));
        database.addOrUpdateEntry(cardModel.createCard("spät dran", "быть поздным"));
        database.addOrUpdateEntry(cardModel.createCard("einsehen", "изучить убедиться, понять"));
        while (true){
            cardModel.getNextCard().get();
            cardModel.getNextCard().get();
            cardModel.getNextCard().get();
            cardModel.getNextCard().get();
        }

    }

    private InputStream read(String path){
        return CardModelImpl.class.getResourceAsStream(path);
    }
}