package coden.decks.core.firebase.user;

import coden.decks.core.user.User;
import coden.decks.core.user.UserDeserializer;
import coden.decks.core.user.UserEntry;
import com.google.cloud.firestore.DocumentSnapshot;

/**
 * Default implementation of {@link UserDeserializer} for the firebase, that
 * converts {@link DocumentSnapshot} to a {@link User}
 *
 * @author Denys Chernyshov
 */
public class FirebaseUserDeserializer implements UserDeserializer<DocumentSnapshot> {
    @Override
    public User deserialize(DocumentSnapshot source) {
        return new UserEntry(source.getId());
    }
}
