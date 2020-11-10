package coden.cards.persistence.firebase;

import coden.cards.config.Config;
import java.io.InputStream;
import java.util.Properties;

public class FirebaseConfig extends Config {

    public final String url = getString("firebase.url");
    public final String userCollection = getString("firebase.userCollection");
    public final String mainCollection = getString("firebase.mainCollection");

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
