package coden.decks.core.firebase;

import static java.util.Objects.requireNonNull;

import coden.decks.core.data.Card;
import coden.decks.core.data.CardDeserializer;
import coden.decks.core.firebase.app.FirebaseAppFactory;
import coden.decks.core.firebase.config.FirebaseConfig;
import coden.decks.core.persistence.Database;
import coden.decks.core.user.User;
import coden.decks.core.user.UserDeserializer;
import coden.decks.core.user.UserNotProvidedException;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Represents a Firebase implementation of the {@link Database}
 */
public class Firebase implements Database {

    /** A mapper that deserializes internal {@link DocumentSnapshot} to {@link Card} */
    private final CardDeserializer<DocumentSnapshot> cardUnmarshaller;
    /** A mapper that deserializes internal {@link DocumentSnapshot} to {@link User} */
    private final UserDeserializer<DocumentSnapshot> userDeserializer;
    /** A firebase config containing path to decks and users */
    private final FirebaseConfig config;
    /** An internal interface to firebase to make the requests */
    private final Firestore firestore;
    /** Created app for this current session of database connection */
    private final FirebaseApp app;

    /** The current user of the collections */
    private User user;
    /** The collection referencing the deck collection containing all the cards */
    private CollectionReference deck;

    /**
     * Creates a new database connection to firebase
     *
     * @param cardDeserializer
     *         to deserialize internal representation of cards to {@link Card}s
     * @param userDeserializer
     *         to deserialize internal representation of user tos {@link User}s
     * @param config
     *         the firebase config
     * @param factory
     *         the firebase factory to create app instances
     */
    public Firebase(CardDeserializer<DocumentSnapshot> cardDeserializer, UserDeserializer<DocumentSnapshot> userDeserializer,
                    FirebaseConfig config, InputStream serviceAccount, FirebaseAppFactory factory) throws Exception {
        this.cardUnmarshaller = requireNonNull(cardDeserializer);
        this.userDeserializer = requireNonNull(userDeserializer);
        this.config = requireNonNull(config);
        this.app = requireNonNull(factory).create(config, serviceAccount);
        this.firestore = FirestoreClient.getFirestore(app);
    }

    /**
     * Returns the current user
     *
     * @return the user, may be {@code null}
     */
    @Override
    public User getUser() {
        return user;
    }

    /**
     * Updates the user, thus changing the deck reference
     *
     * @param newUser
     *         the user to set
     */
    @Override
    public void setUser(User newUser) {
        if (!Objects.equals(this.user, newUser)) {
            this.user = requireNonNull(newUser);
            this.deck = null;
        }
    }

    @Override
    public CompletableFuture<Stream<User>> getAllUsers() {
        ApiFuture<QuerySnapshot> allUsersFuture = firestore.collection(config.userCollection).get();
        return createCompletableFuture(allUsersFuture)
                .thenApply(this::asUsers);
    }

    @Override
    public CompletableFuture<Stream<Card>> getAllEntries() throws UserNotProvidedException {
        ApiFuture<QuerySnapshot> getAllEntriesFuture = getCurrentDeck().get();
        return createCompletableFuture(getAllEntriesFuture).
                thenApply(this::asCards);
    }


    @Override
    public CompletableFuture<Stream<Card>> getGreaterOrEqualLevel(int level) throws UserNotProvidedException {
        ApiFuture<QuerySnapshot> getGreaterOrEqualLevelFuture = getCurrentDeck()
                .whereGreaterThanOrEqualTo("level", level)
                .get();
        return createCompletableFuture(getGreaterOrEqualLevelFuture)
                .thenApply(this::asCards);
    }

    @Override
    public CompletableFuture<Stream<Card>> getLessOrEqualLevel(int level) throws UserNotProvidedException {
        ApiFuture<QuerySnapshot> getLessOrEqualLevelFuture = getCurrentDeck()
                .whereLessThanOrEqualTo("level", level)
                .get();
        return createCompletableFuture(getLessOrEqualLevelFuture)
                .thenApply(this::asCards);
    }

    @Override
    public CompletableFuture<Void> deleteEntry(Card card) throws UserNotProvidedException {
        ApiFuture<WriteResult> deleteFuture = getCurrentDeck()
                .document(card.getFrontSide())
                .delete();
        return createCompletableFuture(deleteFuture)
                .thenApply(writeResult -> null);
    }

    @Override
    public CompletableFuture<Void> addOrUpdateEntry(Card card) {
        ApiFuture<WriteResult> addOrUpdateFuture = getCurrentDeck()
                .document(card.getFrontSide())
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
    private CollectionReference getCurrentDeck() {
        if (deck == null) {
            deck = requestDeckCollection();
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
    private CollectionReference requestDeckCollection() {
        if (user == null) throw new UserNotProvidedException();
        return firestore.collection(config.userCollection)
                .document(user.getName())
                .collection(config.deckCollection);
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
     *         the snapshot to convert
     * @return the stream of cards contained by the given query snapshot
     */
    private Stream<Card> asCards(QuerySnapshot snapshot) {
        return snapshot.getDocuments().stream().map(cardUnmarshaller::deserialize);
    }

    /**
     * Converts the given {@link QuerySnapshot} to stream of {@link User}s
     *
     * @param snapshot
     *         the snapshot to convert
     * @return the stream of users contained by the given query snapshot
     */
    private Stream<User> asUsers(QuerySnapshot snapshot) {
        return snapshot.getDocuments().stream().map(userDeserializer::deserialize);
    }

    /**
     * Closes the firebase and deletes the created app.
     */
    @Override
    public void close() throws Exception {
        firestore.close();
        app.delete();
    }
}
