package coden.cards.persistence.firebase;

import coden.cards.data.Card;
import coden.cards.persistence.Database;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class Firebase implements Database {

    private final String username;
    private final FirebaseConfig config;
    private final Firestore firestore;

    private final CollectionReference cards;

    public Firebase(String username, InputStream serviceAccount, InputStream config) throws IOException {
        Objects.requireNonNull(serviceAccount);
        Objects.requireNonNull(config);
        Objects.requireNonNull(username);
        this.username = username;
        this.config = new FirebaseConfig(config);
        this.firestore = createFirestore(serviceAccount, this.config.url);
        this.cards = createCollection();
    }

    private Firestore createFirestore(InputStream serviceAccount, String url) throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(url)
                .build();

        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }

    private CollectionReference createCollection() {
        return firestore.collection(this.config.userCollection)
                .document(this.username)
                .collection(this.config.mainCollection);
    }

    @Override
    public Stream<Card> getAllEntries() throws ExecutionException, InterruptedException {
        return cards.get()
                .get()
                .getDocuments()
                .stream()
                .map(c -> c.toObject(FirebaseCardEntry.class));
    }


    @Override
    public Stream<Card> getGreaterOrEqualLevel(int level) throws ExecutionException, InterruptedException {
        return cards.whereGreaterThanOrEqualTo("level", level)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(c -> c.toObject(FirebaseCardEntry.class));
    }

    @Override
    public Stream<Card> getLessOrEqualLevel(int level) throws ExecutionException, InterruptedException {
        return cards.whereLessThanOrEqualTo("level", level)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(c -> c.toObject(FirebaseCardEntry.class));
    }

    @Override
    public void deleteEntry(Card entry) throws ExecutionException, InterruptedException {
        cards.document(entry.getFirstSide())
                .delete()
                .get();
    }

    @Override
    public void addOrUpdateEntry(Card entry) throws ExecutionException, InterruptedException {
        cards.document(entry.getFirstSide())
                .set(entry)
                .get();
    }
}