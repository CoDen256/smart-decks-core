package coden.decks.core.model;

import coden.decks.core.data.Card;
import coden.decks.core.firebase.Firebase;
import coden.decks.core.firebase.FirebaseConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;

public class FirebaseTest {



    @Test
    void relocateEverything() throws IOException {
        Firebase firebase = new Firebase(
                new FirebaseConfig(FirebaseTest.class.getResourceAsStream("/firebase_test.cfg")),
                FirebaseTest.class.getResourceAsStream("/serviceAccountTest.json"));
        Firebase deprecated = new Firebase(
                new FirebaseConfig(FirebaseTest.class.getResourceAsStream("/firebase_test_deprecated.cfg")),
                FirebaseTest.class.getResourceAsStream("/serviceAccountTest_deprecated.json"));



    }

    public class DeprecatedFirebase extends Firebase{

        /**
         * Creates a new {@code Firebase}
         *
         * @param config
         *         the firebase configuration
         * @param serviceAccount
         *         the service account configuration
         * @throws IOException
         *         on creating a {@link Firestore}
         */
        public DeprecatedFirebase(FirebaseConfig config, InputStream serviceAccount) throws IOException {
            super(config, serviceAccount);
        }


    }

    public class DeprecatedFirebaseCard implements Card {

        /** The front side field mapping */
        private String firstSide;
        /** The back side field mapping */
        private String secondSide;
        /** The level of the card mapping */
        private int level;

        /**
         * The the stored in firebase mapping of {@link Instant}.
         * The map contains fields like 'epochSecond' and 'nano' that can be converted to {@link Instant}
         */
        private Map<String, Long> lastReview;

        /** A private constructor for deserialization */
        DeprecatedFirebaseCard() {
        }

        @Override
        public String getFirstSide() {
            return firstSide;
        }

        @Override
        public String getSecondSide() {
            return secondSide;
        }

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public Instant getLastReview() {
            return Instant.ofEpochSecond(lastReview.get("epochSecond"), lastReview.get("nano"));
        }

        @Override
        public String toString() {
            return String.format("Card<%s:%s>", firstSide, secondSide);
        }
    }
}
