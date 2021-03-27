package coden.decks.core.data;

/**
 * Represents a card deserializer, that deserializes internal representation of type <T>
 * to {@link Card}
 *
 * @param <T>
 *         type of internal representation of {@link Card}
 */
@FunctionalInterface
public interface CardDeserializer<T> {
    /**
     * Deserializes given source of type <T> to {@link Card}
     *
     * @param source
     *         the internal representation
     * @return a {@link Card}
     */
    Card deserialize(T source);
}
