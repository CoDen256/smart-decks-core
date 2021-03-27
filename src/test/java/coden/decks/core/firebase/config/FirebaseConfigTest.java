package coden.decks.core.firebase.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;

class FirebaseConfigTest {


    @Test
    void testParseBaseConfig() throws FileNotFoundException {
        InputStream configFile = readJson("/firebase_test.cfg");
        FirebaseConfig firebaseConfig = new FirebaseConfig(configFile);

        assertEquals("deck", firebaseConfig.deckCollection);
        assertEquals("users", firebaseConfig.userCollection);
        assertEquals("https://smart-decks-5555.firebaseio.com", firebaseConfig.url);
        assertEquals("smart-decks-test", firebaseConfig.name);
    }

    private InputStream readJson(String filename){
        return getClass().getResourceAsStream(filename);
    }
}