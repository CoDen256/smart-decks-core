package coden.core.decks.revision;

import java.util.List;

/**
 * Represents the entry in revision config. The config will be deserialized to multiple
 * revision config entries.
 */
public class RevisionConfigEntry {
    /** The string representing time to next revision for the given list of levels */
    private String delayToRevision;
    /** The list of levels to which the given delay applies */
    private List<Integer> levels;

    RevisionConfigEntry(){}

    public String getDelayToRevision() {
        return delayToRevision;
    }

    public void setDelayToRevision(String delayToRevision) {
        this.delayToRevision = delayToRevision;
    }

    public List<Integer> getLevels() {
        return levels;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }
}
