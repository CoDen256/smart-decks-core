package coden.core.decks.reminder;

import java.util.List;

public class ReminderLevelEntry {
    private String durationString;
    private List<Integer> levels;

    ReminderLevelEntry(){}


    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public List<Integer> getLevels() {
        return levels;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }
}
