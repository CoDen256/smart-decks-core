package coden.cards.reminder;

import java.util.List;

public class ReminderLevelEntry {
    private int amount;
    private String unit;
    private List<Integer> levels;

    ReminderLevelEntry(){}

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<Integer> getLevels() {
        return levels;
    }

    public void setLevels(List<Integer> levels) {
        this.levels = levels;
    }
}
