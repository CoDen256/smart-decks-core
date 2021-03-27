package coden.decks.core.data;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.time.Instant;

class SimpleCardTest {

    @Test
    void testBuilder() {
        assertThrows(NullPointerException.class, () -> new SimpleCard.Builder()
                .setBackSide("something")
                .setLastReview(Instant.now())
                .setLevel(0)
                .create());

        assertThrows(NullPointerException.class, () -> new SimpleCard.Builder()
                .setBackSide("something")
                .setLevel(0)
                .setFrontSide("something")
                .create());
        assertThrows(NullPointerException.class, () -> new SimpleCard.Builder()
                .setBackSide("something")
                .setLevel(0)
                .setLastReview(Instant.now())
                .create());
        assertThrows(IllegalArgumentException.class, () -> new SimpleCard.Builder()
                .setBackSide("back")
                .setFrontSide("front")
                .setLastReview(Instant.now())
                .create());
    }
}