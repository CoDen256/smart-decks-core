package coden.decks.core.data;

import coden.decks.core.data.Card;

/**
 * Represents a card mapper, that maps internal representation of {@link Card}
 * to {@link Card}
 *
 * @param <T>
 *         type of internal representation of {@link Card}
 */
@FunctionalInterface
public interface CardMapper<T> {
    /**
     * Maps given source of type <T> to {@link Card}
     *
     * @param source
     *         the internal representation
     * @return a {@link Card}
     */
    Card map(T source);
}
