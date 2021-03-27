package coden.decks.core.firebase.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class FirebaseCardDeserializeTest {
    @Test
    void testDeserialize() throws JsonProcessingException {
        ObjectMapper objectMapper = new JsonMapper();
        String cardJson = "{"
                + "\"frontSide\":\"front\","
                + "\"backSide\":\"back\","
                + "\"level\":1,"
                + "\"lastReview\":{"
                + "   \"epochSecond\":1234567,"
                + "    \"nano\":1234}"
                + "}";
        FirebaseCard card = objectMapper.readValue(cardJson, FirebaseCard.class);
        assertEquals("front", card.getFrontSide());
        assertEquals("back", card.getBackSide());
        assertEquals(1, card.getLevel());
        assertEquals(Instant.ofEpochSecond(1234567, 1234), card.getLastReview());
    }
}