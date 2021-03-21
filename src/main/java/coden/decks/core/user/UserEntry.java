package coden.decks.core.user;

import java.util.Objects;

/**
 * The particular instance of the {@link User}
 */
public class UserEntry implements User {

    /** The name of the user */
    private final String name;

    /**
     * Creates a new user from the given name
     * @param name the name of the user
     */
    public UserEntry(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntry)) return false;
        UserEntry userEntry = (UserEntry) o;
        return name.equals(userEntry.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("User<%s>", name);
    }
}
