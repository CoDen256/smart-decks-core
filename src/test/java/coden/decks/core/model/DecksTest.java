package coden.decks.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import coden.decks.core.data.Card;
import coden.decks.core.data.SimpleCard;
import coden.decks.core.persistence.Database;
import coden.decks.core.revision.RevisionManager;
import coden.decks.core.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DecksTest {

    @Mock
    private Database database;

    @Mock
    private RevisionManager revisor;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    @BeforeEach
    void setUp() {
        Mockito.reset(database);
        Mockito.reset(revisor);
        Mockito.reset(user);
    }

    @Test
    void testCreateDecks() {
        new Decks(user, revisor, database);
        verify(database, times(1)).setUser(user);
    }

    @Test
    void testCreateCard(){
        //setup
        when(revisor.getMinLevel()).thenReturn(10);

        // exercise
        Decks decks = new Decks(user, revisor, database);
        Card card = decks.createCard("front", "back");
        Instant creationTime = Instant.now();

        // verify
        verify(revisor, times(1)).getMinLevel();
        verify(database, times(1)).setUser(user);
        verifyNoMoreInteractions(database);
        verifyNoMoreInteractions(user);
        verifyNoMoreInteractions(revisor);

        assertEquals("front", card.getFrontSide());
        assertEquals("back", card.getBackSide());
        assertEquals(10, card.getLevel());
        assertTrue(Math.abs(creationTime.getEpochSecond() - card.getLastReview().getEpochSecond()) < 10);
    }


    @Test
    void testGetFrontAndBackSide(){
        //setup
        Card card = Mockito.mock(Card.class);
        when(card.getFrontSide()).thenReturn("<FRONT>");
        when(card.getBackSide()).thenReturn("<BACK>");

        //exercise
        Decks decks = new Decks(user, revisor, database);
        String frontSide = decks.getFrontSide(card);
        String backSide = decks.getBackSide(card);

        //verify
        verify(database, times(1)).setUser(user);
        verifyNoInteractions(revisor);
        verifyNoMoreInteractions(database);
        verifyNoMoreInteractions(user);

        verify(card).getBackSide();
        verify(card).getFrontSide();

        assertEquals(frontSide, "<FRONT>");
        assertEquals(backSide, "<BACK>");

    }


    @Test
    void testSetKnowAndDontKnow(){
        // setup
        when(revisor.getMinLevel()).thenReturn(0);
        when(revisor.getMaxLevel()).thenReturn(10);
        Decks decks = new Decks(user, revisor, database);
        Card card = new SimpleCard.Builder()
                .setFrontSide("front")
                .setBackSide("back")
                .setLevel(2)
                .setLastReview(Instant.now())
                .create();

        // exercise
        decks.setKnow(card);

        // verify
        verify(revisor, times(1)).getMaxLevel();
        verify(database, times(1)).addOrUpdateEntry(cardCaptor.capture());

        assertEquals(card.getLevel() + 1, cardCaptor.getValue().getLevel());

        Mockito.reset(database);
        // exercise
        decks.setDontKnow(card);


        // verify
        verify(revisor, times(1)).getMinLevel();
        verify(database, times(1)).addOrUpdateEntry(cardCaptor.capture());

        assertEquals(card.getLevel() - 1, cardCaptor.getValue().getLevel());
    }




    @Test
    void testAddCard(){
        //setup
        Card card = Mockito.mock(Card.class);

        //exercise
        new Decks(user, revisor, database).addCard(card);

        //verify
        verify(database).addOrUpdateEntry(card);
    }


    @Test
    void testDeleteCard(){
        //setup
        Card card = Mockito.mock(Card.class);

        //exercise
        new Decks(user, revisor, database).deleteCard(card);

        //verify
        verify(database).deleteEntry(card);
    }


    @Test
    void testGetNextCard() throws ExecutionException, InterruptedException {
        //setup
        Card card = Mockito.mock(Card.class);
        when(revisor.getMaxLevel()).thenReturn(11);
        when(database.getLessOrEqualLevel(10)).thenReturn(CompletableFuture.supplyAsync(() -> Stream.of(card, card)));
        when(revisor.isReady(card)).thenReturn(true).thenReturn(false);

        //exercise
        CompletableFuture<Card> nextCard = new Decks(user, revisor, database).getNextCard();


        //verify
        assertEquals(card, nextCard.get());
    }


    @Test
    void testGetReadyCards(){
        //setup
        Card card = Mockito.mock(Card.class);
        Decks decks = new Decks(user, revisor, database);
        when(revisor.getMaxLevel()).thenReturn(11);
        when(database.getLessOrEqualLevel(10)).thenReturn(CompletableFuture.supplyAsync(() -> Stream.of(card)));
        //exercise

        decks.getReadyCards();

        //verify
        verify(database, times(1)).getLessOrEqualLevel(10);
        verify(revisor, times(1)).isReady(card);
    }

    @Test
    void testGetPendingCards(){
        //setup
        Card card = Mockito.mock(Card.class);
        Decks decks = new Decks(user, revisor, database);
        when(revisor.getMaxLevel()).thenReturn(11);
        when(database.getLessOrEqualLevel(10)).thenReturn(CompletableFuture.supplyAsync(() -> Stream.of(card)));
        //exercise

        decks.getPendingCards();

        //verify
        verify(database, times(1)).getLessOrEqualLevel(10);
        verify(revisor, times(1)).isReady(card);
    }

    @Test
    void testGetDoneCards(){
        //setup
        Card card = Mockito.mock(Card.class);
        Decks decks = new Decks(user, revisor, database);
        when(revisor.getMaxLevel()).thenReturn(11);
        when(database.getGreaterOrEqualLevel(11)).thenReturn(CompletableFuture.supplyAsync(() -> Stream.of(card)));
        //exercise

        decks.getDoneCards();

        //verify
        verify(database, times(1)).getGreaterOrEqualLevel(11);
        verify(revisor, times(1)).getMaxLevel();
        verifyNoMoreInteractions(revisor);
    }

    @Test
    void testGetAllCards(){
        //setup
        Card card = Mockito.mock(Card.class);
        Decks decks = new Decks(user, revisor, database);
        when(database.getAllEntries()).thenReturn(CompletableFuture.supplyAsync(() -> Stream.of(card)));

        //exercise

        decks.getAllCards();

        //verify
        verify(database, times(1)).getAllEntries();
        verifyNoInteractions(revisor);
    }

    @Test
    void testSetUser(){
        //setup
        DecksModel decksModel = new Decks(user, revisor, database);

        //exercise
        decksModel.setUser(user);

        //verify
        verify(database, times(2)).setUser(user);
    }
}
