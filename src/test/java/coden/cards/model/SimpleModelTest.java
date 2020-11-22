package coden.cards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.persistence.Database;
import coden.cards.persistence.firebase.Firebase;
import coden.cards.reminder.Reminder;
import coden.cards.user.UserEntry;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
        final SimpleModel cardModel = new SimpleModel(getRandomUser(), reminder, firebase);
        final Card card = createRandomCard(cardModel);
        cardModel.addCard(card).get(5, TimeUnit.SECONDS);
        final CompletableFuture<List<Card>> cardsFuture = cardModel.getAllCards();
        final List<Card> cards = cardsFuture.get(5, TimeUnit.SECONDS);
        assertEquals(1, cards.size());
        final Card actual = cards.get(0);
        assertEquals(card.getFirstSide(), actual.getFirstSide());
        assertEquals(card.getSecondSide(), actual.getSecondSide());
        assertEquals(card.getLevel(), actual.getLevel());
        assertEquals(card.getLastReview(), actual.getLastReview());
    }

    @Test
    void testDeleteEntry() throws Exception {
        final SimpleModel cardModel = new SimpleModel(getRandomUser(), reminder, firebase);
        final Card card = createRandomCard(cardModel);
        cardModel.addCard(card).get(5, TimeUnit.SECONDS);
        cardModel.deleteCard(card).get(5, TimeUnit.SECONDS);
        final CompletableFuture<List<Card>> allCardsFuture = cardModel.getAllCards();
        final List<Card> cards = allCardsFuture.get(5, TimeUnit.SECONDS);
        assertEquals(0, cards.size());
    }


    @Test
    void testComplexQueries() throws Exception {
        final SimpleModel cardModel = new SimpleModel(getRandomUser(), reminder, firebase);

        final Card card = new CardEntry.Builder()
                .setFirstSide("einsehen")
                .setSecondSide("понять, изучить, убедиться")
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
        assertEquals("einsehen", doneCard.getFirstSide());
        assertEquals("понять, изучить, убедиться", doneCard.getSecondSide());
        assertTrue(card.getLastReview().isBefore(doneCard.getLastReview()));
    }

    @Test
    void testGetNext() throws InterruptedException, ExecutionException, TimeoutException {
        // Setup
        final UserEntry randomUser = getRandomUser();
        final Model model = new SimpleModel(randomUser, reminder, firebase);

        final Card newCard1 = new CardEntry.Builder(createRandomCard(model))
                .setFirstSide("15 Minutes")
                .setLastReview(Instant.now().minus(15, ChronoUnit.MINUTES))
                .create();

        final Card newCard2 = new CardEntry.Builder(createRandomCard(model))
                .setFirstSide("10 Minutes")
                .setLastReview(Instant.now().minus(10, ChronoUnit.MINUTES))
                .create();

        final Card newCard3 = new CardEntry.Builder(createRandomCard(model))
                .setFirstSide("5 Minutes")
                .setLastReview(Instant.now().minus(5, ChronoUnit.MINUTES))
                .create();

        model.addCard(newCard1).get(5, TimeUnit.SECONDS);
        model.addCard(newCard2).get(5, TimeUnit.SECONDS);
        model.addCard(newCard3).get(5, TimeUnit.SECONDS);
        assertTrue(reminder.shouldRemind(newCard1));
        assertTrue(reminder.shouldRemind(newCard2));
        assertTrue(reminder.shouldRemind(newCard3));

        // Execute
        final CachedModel cachedCardModel = new CachedModel(randomUser, reminder, firebase, 1);
        final CompletableFuture<List<Card>> readyCards = cachedCardModel.getReadyCards();
        final List<Card> cards = readyCards.get(5, TimeUnit.SECONDS);
        assertEquals(3, cards.size());
        final Card card = cards.get(0);
        assertEquals(newCard1.getFirstSide(), card.getFirstSide());
        assertEquals(newCard1.getFirstSide(), cachedCardModel.getNextCard()
                .get(5, TimeUnit.SECONDS).getFirstSide());
        assertEquals(newCard2.getFirstSide(), cachedCardModel.getNextCard()
                .get(5, TimeUnit.SECONDS).getFirstSide());
        assertEquals(newCard3.getFirstSide(), cachedCardModel.getNextCard()
                .get(5, TimeUnit.SECONDS).getFirstSide());
    }

    private UserEntry getRandomUser() {
        return new UserEntry(UUID.randomUUID().toString());
    }

    private Card createRandomCard(Model model) {
        return model.createCard(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    private static InputStream read(String path){
        return SimpleModel.class.getResourceAsStream(path);
    }
}