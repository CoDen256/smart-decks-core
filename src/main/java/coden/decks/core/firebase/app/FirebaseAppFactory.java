package coden.decks.core.firebase.app;

import coden.decks.core.firebase.config.FirebaseConfig;
import com.google.firebase.FirebaseApp;

import java.io.InputStream;

/**
 * The {@code FirebaseAppFactory} is a factory to create a FirebaseApp from the given
 * {@link FirebaseConfig}
 *
 * @author Denys Chernyshov
 */
@FunctionalInterface
public interface FirebaseAppFactory {
    /**
     * Creates a new {@link FirebaseApp} from the given {@link FirebaseConfig}
     *
     * @param config
     *         the firebase config
     * @return a new firebase app
     */
    FirebaseApp create(FirebaseConfig config, InputStream serviceAccount) throws Exception;
}
