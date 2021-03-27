package coden.decks.core.firebase.app;

import coden.decks.core.firebase.config.FirebaseConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.IOException;
import java.io.InputStream;

/**
 * The {@code FirebaseAppFactoryImpl} is a base implementation of {@link FirebaseAppFactory}
 * to create a {@link FirebaseApp} from the given config.
 *
 * @author Denys Chernyshov
 */
public class FirebaseAppFactoryImpl implements FirebaseAppFactory {
    /**
     * Creates a new Firebase app from the given firebase configuration
     *
     * @param config
     *         the firebase config that contains url, service account and name of the app
     * @return a new firebase app
     * @throws IOException
     *         if reading from the config fails
     */
    @Override
    public FirebaseApp create(FirebaseConfig config, InputStream serviceAccount) throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(config.url)
                .build();

        return FirebaseApp.initializeApp(options, config.name);
    }
}
