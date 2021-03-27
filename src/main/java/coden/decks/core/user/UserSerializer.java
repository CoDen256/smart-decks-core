package coden.decks.core.user;

/**
 * Represents a user mapper, that serializes the given {@link User} to
 * to some other representation of type <T>
 *
 * @param <T>
 *         type of internal representation of {@link User}
 */
@FunctionalInterface
public interface UserSerializer<T> {
    /**
     * Serializes given {@link User} to type <T>
     *
     * @param source
     *         the User to serialize
     * @return a target type <T>
     */
    T serialize(User source);
}