package coden.decks.core.firebase;

import coden.decks.core.config.Config;
import java.io.InputStream;
import java.util.Properties;

/**
 * Represents a firebase config, that will be used to identify firebase url
 * name of user and decks collection for particular user
 */
public class FirebaseConfig extends Config {

    /** The url of the firebase */
    public final String url = getString("firebase.url");

    /** The user collection containing decks */
    public final String userCollection = getString("firebase.userCollection");
    /** The decks collection */
    public final String deckCollection = getString("firebase.deckCollection");

    public FirebaseConfig(String path) {
        super(path);
    }

    public FirebaseConfig(Properties properties) {
        super(properties);
    }

    public FirebaseConfig(InputStream is) {
        super(is);
    }
}
