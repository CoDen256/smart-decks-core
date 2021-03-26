package coden.decks.core.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.UUID;

class UserEntryTest {

    @Test
    void getName() {
        String randomName = UUID.randomUUID().toString();
        assertEquals(randomName, new UserEntry(randomName).getName());
        assertEquals("user", new UserEntry("user").getName());
    }

    @Test
    void testEquals() {
        String randomName = UUID.randomUUID().toString();
        assertEquals(new UserEntry(randomName), new UserEntry(randomName));
        assertEquals(new UserEntry("user"), new UserEntry("user"));
    }
}