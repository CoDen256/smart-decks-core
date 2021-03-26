package coden.decks.core.firebase;

import static org.junit.jupiter.api.Assertions.*;

import coden.decks.core.config.Config;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

class FirebaseConfigTest {


    @Test
    void testParseConfig() {
        InputStream configFile = readJson("/firebase_test.cfg");
        FirebaseConfig firebaseConfig = new FirebaseConfig(configFile);

        assertEquals("deck", firebaseConfig.deckCollection);
        assertEquals("users", firebaseConfig.userCollection);
        assertEquals("https://smart-decks-5555.firebaseio.com", firebaseConfig.url);

    }

    private InputStream readJson(String filename){
        return getClass().getResourceAsStream(filename);
    }
}