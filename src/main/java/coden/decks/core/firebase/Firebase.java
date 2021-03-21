package coden.decks.core.firebase;

import coden.decks.core.data.Card;
import coden.decks.core.persistence.Database;
import coden.decks.core.user.User;
import coden.decks.core.user.UserNotProvidedException;
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

/**
 * Represents a Firebase implementation of the {@link Database}
 */
public class Firebase implements Database {

    /** The firebase config */
    private final FirebaseConfig config;
    /** The internal interface to make the requests */
    private final Firestore firestore;

    /** The current user of the collections */
    private User user;
    /** The collection referencing the deck collection containing all the cards */
    private CollectionReference deck;

    /**
     * Creates a new {@code Firebase}
     *
     * @param serviceAccount
     *         the service account configuration
     * @param config
     *         the firebase configuration
     * @throws IOException
     *         on creating a {@link Firestore}
     */
    public Firebase(FirebaseConfig config, InputStream serviceAccount) throws IOException {
        Objects.requireNonNull(serviceAccount);
        Objects.requireNonNull(config);
        this.config = config;
        this.firestore = createFirestore(serviceAccount, config.url);
    }

    /**
     * Utility method that creates a {@link Firestore} from the given service account and url
     *
     * @param serviceAccount
     *         the service account
     * @param url
     *         the url of the firebase
     * @return a new {@link Firestore} to manage firebase
     * @throws IOException
     *         on reading service account
     */
    private Firestore createFirestore(InputStream serviceAccount, String url) throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(url)
                .build();

        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }

    /**
     * Updates the user, thus changing the deck reference
     *
     * @param user
     *         the user to change
     */
    @Override
    public void setUser(User user) {
        if (!Objects.equals(this.user, user)) {
            this.user = Objects.requireNonNull(user);
            this.deck = null;
        }
    }

    @Override
    public CompletableFuture<Stream<Card>> getAllEntries() throws UserNotProvidedException {
        ApiFuture<QuerySnapshot> getAllEntriesFuture = getDeck().get();
        return createCompletableFuture(getAllEntriesFuture).
                thenApply(this::fetchDocumentsAsCards);
    }


    @Override
    public CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level) throws UserNotProvidedException {
        ApiFuture<QuerySnapshot> getGreaterOrEqualLevelFuture = getDeck()
                .whereGreaterThanOrEqualTo("level", level)
                .get();
        return createCompletableFuture(getGreaterOrEqualLevelFuture)
                .thenApply(this::fetchDocumentsAsCards);
    }

    @Override
    public CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level) throws UserNotProvidedException {
        ApiFuture<QuerySnapshot> getLessOrEqualLevelFuture = getDeck()
                .whereLessThanOrEqualTo("level", level)
                .get();
        return createCompletableFuture(getLessOrEqualLevelFuture)
                .thenApply(this::fetchDocumentsAsCards);
    }

    @Override
    public CompletableFuture<Void> deleteEntry(Card card) throws UserNotProvidedException {
        ApiFuture<WriteResult> deleteFuture = getDeck()
                .document(card.getFirstSide())
                .delete();
        return createCompletableFuture(deleteFuture)
                .thenApply(writeResult -> null);
    }

    @Override
    public CompletableFuture<Void> addOrUpdateEntry(Card card) {
        ApiFuture<WriteResult> addOrUpdateFuture = getDeck()
                .document(card.getFirstSide())
                .set(card);
        return createCompletableFuture(addOrUpdateFuture)
                .thenApply(writeResult -> null);
    }

    /**
     * Returns the working deck collection for the given user. If it is not created
     * creates a new deck.
     *
     * @return the deck
     */
    private CollectionReference getDeck() {
        if (deck == null) {
            deck = createCollection();
        }
        return deck;
    }

    /**
     * Helper method to update a {@link CollectionReference} for the specified user
     *
     * @return a new collection reference to deck collection
     * @throws UserNotProvidedException
     *         if user was not set
     */
    private CollectionReference createCollection() {
        if (user == null) throw new UserNotProvidedException();
        return firestore.collection(this.config.userCollection)
                .document(this.user.getName())
                .collection(this.config.deckCollection);
    }

    /**
     * Converts a {@link CompletableFuture} from the given {@link ApiFuture}
     *
     * @param querySnapshotApiFuture
     *         the api future provided by firebase api
     * @return a new {@link CompletableFuture}
     */
    private <T> CompletableFuture<T> createCompletableFuture(ApiFuture<T> querySnapshotApiFuture) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        ApiFutures.addCallback(querySnapshotApiFuture, createCallback(completableFuture), MoreExecutors.directExecutor());
        return completableFuture;
    }

    /**
     * Creates a callback that will be added to {@link ApiFuture} completing the give
     * {@link CompletableFuture}
     *
     * @param completableFuture
     *         the completable future to pass the result to
     * @param <T>
     *         the result type of completable future and api future callback
     * @return the new api future callback that will trigger completable future on success
     */
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

    /**
     * Converts the given {@link QuerySnapshot} to stream of {@link Card}s
     *
     * @param snapshot
     *         the snapshot to fetch
     * @return the stream of cards contained by the given query snapshot
     */
    private Stream<Card> fetchDocumentsAsCards(QuerySnapshot snapshot) {
        return snapshot.getDocuments()
                .stream()
                .map(this::toFirebaseCardEntry);
    }

    /**
     * Converts a {@link QueryDocumentSnapshot} to {@link FirebaseCard} by deserializing
     * the response
     *
     * @param snapshot
     *         the document snapshot
     * @return the firebase card
     */
    protected Card toFirebaseCardEntry(QueryDocumentSnapshot snapshot) {
        return snapshot.toObject(FirebaseCard.class);
    }
}
