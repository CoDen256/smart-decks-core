package coden.decks.core.firebase.config;

import coden.decks.core.config.Config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Represents a firebase config, that will be used to identify firebase url
 * name of user and decks collection for particular user
 *
 * @author Denys Chernyshov
 */
public class FirebaseConfig extends Config {
    /** Name of the particular database instance */
    public final String name = getString("firebase.name");
    /** The url of the firebase */
    public final String url = getString("firebase.url");
    /** The user collection containing decks */
    public final String userCollection = getString("firebase.userCollection");
    /** The decks collection */
    public final String deckCollection = getString("firebase.deckCollection");

    public FirebaseConfig(String path) throws FileNotFoundException {
        super(path);
    }

    public FirebaseConfig(Properties properties) throws FileNotFoundException {
        super(properties);
    }

    public FirebaseConfig(InputStream is) throws FileNotFoundException {
        super(is);
    }
}
