package coden.core.decks.revision;

import java.time.Duration;
import java.util.Objects;

/**
 * The actual level of revision.
 */
public class RevisionLevel {
    /** The level of corresponding revision level */
    private int level;
    /** The time to next revision for the given level */
    private Duration delayToNextRevision;

    public RevisionLevel(int level, Duration delayToNextRevision) {
        this.level = level;
        this.delayToNextRevision = delayToNextRevision;
    }

    public Duration getDelayToNextRevision() {
        return delayToNextRevision;
    }

    public void setDelayToNextRevision(Duration delayToNextRevision) {
        this.delayToNextRevision = delayToNextRevision;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RevisionLevel)) return false;
        RevisionLevel that = (RevisionLevel) o;
        return level == that.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }
}
