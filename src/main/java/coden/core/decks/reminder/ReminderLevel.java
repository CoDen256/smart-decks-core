package coden.core.decks.reminder;

import java.time.Duration;
import java.util.Objects;

public class ReminderLevel {
    private int level;
    private Duration delayToNextRevision;

    public ReminderLevel(int level, Duration delayToNextRevision) {
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
        if (!(o instanceof ReminderLevel)) return false;
        ReminderLevel that = (ReminderLevel) o;
        return level == that.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }
}
