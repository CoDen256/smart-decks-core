package coden.decks.core.model;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import coden.decks.core.persistence.Database;
import coden.decks.core.revision.RevisionManager;
import coden.decks.core.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DecksTest {

    @Mock
    private Database database;

    @Mock
    private RevisionManager manager;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        Mockito.reset(database);
        Mockito.reset(manager);
        Mockito.reset(user);
    }

    @Test
    void testCreateDecks() {
        Decks decks = new Decks(user, manager, database);
        verify(database, times(1)).setUser(user);
    }
}
