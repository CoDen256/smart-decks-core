package coden.core.decks.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import coden.core.decks.data.Card;
import coden.core.decks.data.SimpleCard;
import coden.core.decks.persistence.Database;
import coden.core.decks.persistence.firebase.Firebase;
import coden.core.decks.reminder.Reminder;
import coden.core.decks.user.UserEntry;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SimpleModelTest {

    private static Reminder reminder = null;
    private static Database firebase = null;

    @BeforeAll
    static void beforeAll() throws Exception{
        reminder = new Reminder(read("/reminder_test.json"));
        firebase = new Firebase(
                read("/serviceAccountTest.json"),
                read("/firebase_test.cfg"));
    }

    @Test
    void testAddAndGet() throws Exception {
        final SimpleDecks cardModel = new SimpleDecks(getRandomUser(), reminder, firebase);
        final Card card = createRandomCard(cardModel);
        cardModel.addCard(card).get(5, TimeUnit.SECONDS);
        final CompletableFuture<List<Card>> cardsFuture = cardModel.getAllCards();
        final List<Card> cards = cardsFuture.get(5, TimeUnit.SECONDS);
        assertEquals(1, cards.size());
        final Card actual = cards.get(0);
        assertEquals(card.getFrontSide(), actual.getFrontSide());
        assertEquals(card.getBackSide(), actual.getBackSide());
        assertEquals(card.getLevel(), actual.getLevel());
        assertEquals(card.getLastReview(), actual.getLastReview());
    }

    @Test
    void testDeleteEntry() throws Exception {
        final SimpleDecks cardModel = new SimpleDecks(getRandomUser(), reminder, firebase);
        final Card card = createRandomCard(cardModel);
        cardModel.addCard(card).get(5, TimeUnit.SECONDS);
        cardModel.deleteCard(card).get(5, TimeUnit.SECONDS);
        final CompletableFuture<List<Card>> allCardsFuture = cardModel.getAllCards();
        final List<Card> cards = allCardsFuture.get(5, TimeUnit.SECONDS);
        assertEquals(0, cards.size());
    }


    @Test
    void testComplexQueries() throws Exception {
        final SimpleDecks cardModel = new SimpleDecks(getRandomUser(), reminder, firebase);

        final Card card = new SimpleCard.Builder()
                .setFrontSide("einsehen")
                .setBackSide("понять, изучить, убедиться")
                .setLevel(reminder.getMaxLevel())
                .setLastReview(Instant.now())
                .create();

        cardModel.addCard(card).get(5, TimeUnit.SECONDS);
        cardModel.setKnow(card).get(5, TimeUnit.SECONDS);

        final CompletableFuture<List<Card>> learnedCardsFuture = cardModel.getDoneCards();
        final List<Card> learnedCards = learnedCardsFuture.get(5, TimeUnit.SECONDS);
        assertEquals(1, learnedCards.size());
        final Card doneCard = learnedCards.get(0);
        assertEquals(reminder.getMaxLevel(), doneCard.getLevel());
        assertEquals("einsehen", doneCard.getFrontSide());
        assertEquals("понять, изучить, убедиться", doneCard.getBackSide());
        assertTrue(card.getLastReview().isBefore(doneCard.getLastReview()));
    }

    @Test
    void testGetNext() throws InterruptedException, ExecutionException, TimeoutException {
        // Setup
        final UserEntry randomUser = getRandomUser();
        final DecksModel decksModel = new SimpleDecks(randomUser, reminder, firebase);

        final Card newCard1 = new SimpleCard.Builder(createRandomCard(decksModel))
                .setFrontSide("15 Minutes")
                .setLastReview(Instant.now().minus(15, ChronoUnit.MINUTES))
                .create();

        final Card newCard2 = new SimpleCard.Builder(createRandomCard(decksModel))
                .setFrontSide("10 Minutes")
                .setLastReview(Instant.now().minus(10, ChronoUnit.MINUTES))
                .create();

        final Card newCard3 = new SimpleCard.Builder(createRandomCard(decksModel))
                .setFrontSide("5 Minutes")
                .setLastReview(Instant.now().minus(5, ChronoUnit.MINUTES))
                .create();

        decksModel.addCard(newCard1).get(5, TimeUnit.SECONDS);
        decksModel.addCard(newCard2).get(5, TimeUnit.SECONDS);
        decksModel.addCard(newCard3).get(5, TimeUnit.SECONDS);
        assertTrue(reminder.isReady(newCard1));
        assertTrue(reminder.isReady(newCard2));
        assertTrue(reminder.isReady(newCard3));

        // Execute
        final CachedDecks cachedCardModel = new CachedDecks(randomUser, reminder, firebase, 1);
        final CompletableFuture<List<Card>> readyCards = cachedCardModel.getReadyCards();
        final List<Card> cards = readyCards.get(5, TimeUnit.SECONDS);
        assertEquals(3, cards.size());
        final Card card = cards.get(0);
        assertEquals(newCard1.getFrontSide(), card.getFrontSide());
        Assertions.assertEquals(newCard1.getFrontSide(), cachedCardModel.getNextCard()
                .get(5, TimeUnit.SECONDS).getFrontSide());
        Assertions.assertEquals(newCard2.getFrontSide(), cachedCardModel.getNextCard()
                .get(5, TimeUnit.SECONDS).getFrontSide());
        Assertions.assertEquals(newCard3.getFrontSide(), cachedCardModel.getNextCard()
                .get(5, TimeUnit.SECONDS).getFrontSide());
    }

    private UserEntry getRandomUser() {
        return new UserEntry(UUID.randomUUID().toString());
    }

    private Card createRandomCard(DecksModel decksModel) {
        return decksModel.createCard(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    private static InputStream read(String path){
        return SimpleDecks.class.getResourceAsStream(path);
    }
}