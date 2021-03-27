package coden.decks.core.data;

/**
 * Represents a card mapper, that serializes the given {@link Card} to
 * to some other representation of type <T>
 *
 * @param <T>
 *         type of internal representation of {@link Card}
 */
@FunctionalInterface
public interface CardSerializer<T> {
    /**
     * Serializes given {@link Card} to type <T>
     *
     * @param source
     *         the card to serialize
     * @return a target type
     */
    T serialize(Card source);
}