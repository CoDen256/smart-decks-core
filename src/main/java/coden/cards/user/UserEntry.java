package coden.cards.user;

public class UserEntry implements User {

    private final String name;

    public UserEntry(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
