package coden.decks.core.user;

/**
 * Represents a user mapper, that maps internal representation of {@link User}
 * to actual {@link User}} instance
 *
 * @param <T>
 *         type of internal representation of {@link User}
 */
@FunctionalInterface
public interface UserDeserializer<T> {
    /**
     * Maps the source of specified type to {@link User}
     *
     * @param source
     *         the internal representation of the user
     * @return a user
     */
    User deserialize(T source);
}
