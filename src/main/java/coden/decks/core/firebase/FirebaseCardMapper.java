package coden.decks.core.firebase;

import coden.decks.core.data.CardMapper;
import com.google.cloud.firestore.DocumentSnapshot;

/**
 * The {@code FirebaseCardMapper} maps {@link DocumentSnapshot} to {@link FirebaseCard}
 */
public class FirebaseCardMapper implements CardMapper<DocumentSnapshot> {
    @Override
    public FirebaseCard map(DocumentSnapshot source) {
        return source.toObject(FirebaseCard.class);
    }
}
