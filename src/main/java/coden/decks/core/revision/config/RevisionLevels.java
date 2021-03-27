package coden.decks.core.revision.config;

import coden.decks.core.revision.RevisionLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Revision levels represents an array of {@link RevisionLevel} that can be parsed from the given config
 *
 * @author Denys Chernyshov
 */
public class RevisionLevels extends HashSet<RevisionLevel> {
    /** The Object mapper to deserialize config */
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RevisionLevels(Collection<? extends RevisionLevel> c) {
        super(c);
    }

    /**
     * Creates revision levels from the given path to config
     *
     * @param path
     *         the path to config
     * @throws IOException
     *         on parsing the config
     */
    public RevisionLevels(String path) throws IOException {
        this(new FileInputStream(path));
    }

    /**
     * Creates revision levels from the given config
     *
     * @param inputStream
     *         the config
     * @throws IOException
     *         on parsing the config
     */
    public RevisionLevels(InputStream inputStream) throws IOException {
        RevisionConfigEntry[] entries = parseEntries(inputStream);
        for (RevisionConfigEntry entry : entries) {
            for (int level : entry.getLevels()) {
                addLevel(entry.getDelayToRevision(), level);
            }
        }
    }

    /**
     * Parses the {@link RevisionConfigEntry} array from the given file
     */
    private RevisionConfigEntry[] parseEntries(InputStream inputStream) throws IOException {
        return objectMapper.readValue(Objects.requireNonNull(inputStream), RevisionConfigEntry[].class);
    }

    /**
     * Adds a new {@link RevisionLevel} from the given delay to next revision and the given level
     *
     * @param delayToNextRevision
     *         the string representing delay to next revision
     * @param level
     *         the level
     * @throws IdenticalLevelsException
     *         if such a revision level already exists
     */
    private void addLevel(String delayToNextRevision, int level) {
        Duration amount = Duration.parse(delayToNextRevision);
        RevisionLevel newLevel = new RevisionLevel(level, amount);
        if (contains(newLevel)) {
            throw new IdenticalLevelsException("Found two revision level entries with the same level: " + level);
        }
        add(newLevel);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
