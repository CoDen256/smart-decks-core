package coden.cards.persistence.firebase;

import coden.cards.data.Card;
import coden.cards.persistence.Database;
import coden.cards.user.User;
import coden.cards.user.UserNotProvidedException;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Firebase implements Database {

    private final FirebaseConfig config;
    private final Firestore firestore;

    private User user;
    private CollectionReference cards;

    public Firebase(InputStream serviceAccount, InputStream config) throws IOException {
        Objects.requireNonNull(serviceAccount);
        Objects.requireNonNull(config);
        this.config = new FirebaseConfig(config);
        this.firestore = createFirestore(serviceAccount, this.config.url);
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
                .document(this.user.getName())
                .collection(this.config.mainCollection);
    }

    @Override
    public void setUser(User user) {
        this.user = Objects.requireNonNull(user);
        this.cards = createCollection();
    }

    @Override
    public CompletableFuture<Stream<Card>> getAllEntries() throws UserNotProvidedException {
        final ApiFuture<QuerySnapshot> getAllEntriesFuture = getCards().get();
        return createCompletableFuture(getAllEntriesFuture).
                thenApply(this::fetchDocumentsAsFirebaseCardEntries);
    }


    @Override
    public CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level) throws UserNotProvidedException {
        final ApiFuture<QuerySnapshot> getGreaterOrEqualLevelFuture = getCards()
                .whereGreaterThanOrEqualTo("level", level)
                .get();
        return createCompletableFuture(getGreaterOrEqualLevelFuture)
                .thenApply(this::fetchDocumentsAsFirebaseCardEntries);
    }

    @Override
    public CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level) throws UserNotProvidedException{
        final ApiFuture<QuerySnapshot> getLessOrEqualLevelFuture = getCards()
                .whereLessThanOrEqualTo("level", level)
                .get();
        return createCompletableFuture(getLessOrEqualLevelFuture)
                .thenApply(this::fetchDocumentsAsFirebaseCardEntries);
    }

    @Override
    public CompletableFuture<Void> deleteEntry(Card entry) throws UserNotProvidedException{
        final ApiFuture<WriteResult> deleteFuture = getCards()
                .document(entry.getFirstSide())
                .delete();
        return createCompletableFuture(deleteFuture)
                .thenApply(writeResult -> null);
    }

    @Override
    public CompletableFuture<Void> addOrUpdateEntry(Card entry) throws UserNotProvidedException {
        final ApiFuture<WriteResult> addOrUpdateFuture = getCards()
                .document(entry.getFirstSide())
                .set(entry);
        return createCompletableFuture(addOrUpdateFuture)
                .thenApply(writeResult -> null);
    }

    private CollectionReference getCards() throws UserNotProvidedException {
        if (user == null) {
            throw new UserNotProvidedException();
        }
        return cards;
    }

    private <T> CompletableFuture<T> createCompletableFuture(ApiFuture<T> querySnapshotApiFuture) {
        final CompletableFuture<T> completableFuture = new CompletableFuture<>();
        ApiFutures.addCallback(querySnapshotApiFuture, createCallback(completableFuture), MoreExecutors.directExecutor());
        return completableFuture;
    }

    private <T> ApiFutureCallback<T> createCallback(CompletableFuture<T> completableFuture) {
        return new ApiFutureCallback<T>() {
            @Override
            public void onFailure(Throwable t) {
            }

            @Override
            public void onSuccess(T result) {
                completableFuture.complete(result);
            }
        };
    }

    private Stream<Card> fetchDocumentsAsFirebaseCardEntries(QuerySnapshot snapshot) {
        return snapshot.getDocuments()
                .stream()
                .map(this::toFirebaseCardEntry);
    }

    private FirebaseCardEntry toFirebaseCardEntry(QueryDocumentSnapshot c) {
        return c.toObject(FirebaseCardEntry.class);
    }
}
