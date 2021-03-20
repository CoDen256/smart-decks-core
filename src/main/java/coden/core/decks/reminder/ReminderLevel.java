package coden.core.decks.reminder;

import java.time.temporal.TemporalAmount;
import java.util.Objects;

public class ReminderLevel {
    private int level;
    private TemporalAmount temporalAmount;

    public ReminderLevel(int level, TemporalAmount temporalAmount) {
        this.level = level;
        this.temporalAmount = temporalAmount;
    }

    public TemporalAmount getTemporalAmount() {
        return temporalAmount;
    }

    public void setTemporalAmount(TemporalAmount temporalAmount) {
        this.temporalAmount = temporalAmount;
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
