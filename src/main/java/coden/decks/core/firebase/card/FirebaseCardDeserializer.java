package coden.decks.core.firebase.card;

import coden.decks.core.data.CardDeserializer;
import com.google.cloud.firestore.DocumentSnapshot;

/**
 * The {@code FirebaseCardDeserializer} maps {@link DocumentSnapshot} to {@link FirebaseCard}
 */
public class FirebaseCardDeserializer implements CardDeserializer<DocumentSnapshot> {
    @Override
    public FirebaseCard deserialize(DocumentSnapshot source) {
        return source.toObject(FirebaseCard.class);
    }
}
