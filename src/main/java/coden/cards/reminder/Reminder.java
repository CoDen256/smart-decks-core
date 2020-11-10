package coden.cards.reminder;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Reminder {

    private final List<ReminderLevel> levels = new LinkedList<>();

    public Reminder(InputStream is) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ReminderLevelEntry[] entries = objectMapper.readValue(Objects.requireNonNull(is), ReminderLevelEntry[].class);

        for (ReminderLevelEntry entry: entries){
            for (int level: entry.getLevels()){
                addLevel(entry, level);
            }
        }

    }

    private void addLevel(ReminderLevelEntry entry, int level) throws IOException {
        final TemporalAmount amount = parseTemporalAmount(entry.getUnit(), entry.getAmount());
        final ReminderLevel newLevel = new ReminderLevel(level, amount);
        if (levels.contains(newLevel)) throw new IOException("Found two reminder level entries with the same levels");
        levels.add(newLevel);
    }

    private TemporalAmount parseTemporalAmount(String unit, int amount) throws IOException{
        final ChronoUnit chronoUnit = ChronoUnit.valueOf(unit);
        return chronoUnit.getDuration().multipliedBy(amount);
    }

    public TemporalAmount getNextReminderDelay(int level){
        return levels.stream()
                .filter(entry -> entry.getLevel() == level)
                .findAny()
                .map(ReminderLevel::getTemporalAmount)
                .orElseThrow();
    }

    public int getMinLevel(){
        return levels.stream()
                .min(Comparator.comparing(ReminderLevel::getLevel))
                .map(ReminderLevel::getLevel)
                .orElse(0);
    }

    public int getMaxLevel(){
        return levels.stream()
                .max(Comparator.comparing(ReminderLevel::getLevel))
                .map(ReminderLevel::getLevel)
                .orElse(0);
    }
}
