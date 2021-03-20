package coden.core.decks.user;

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
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
