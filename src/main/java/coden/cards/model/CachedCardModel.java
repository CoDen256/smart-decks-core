package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.data.CardEntry;
import coden.cards.persistence.Database;
import coden.cards.reminder.BaseReminder;
import coden.cards.user.User;
import java.time.Instant;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CachedCardModel extends CardModelImpl{

    public static final int MIN_SIZE = 2;

    private final Deque<Card> cache = new LinkedList<>();
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executorService;

    public CachedCardModel(User user, BaseReminder reminder, Database database) {
        super(user, reminder, database);
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::tryFetchAndUpdateCache);

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::tryFetchAndUpdateCache, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public synchronized Card getNextCard() throws Exception {
        try {
            return cache.pop();
        }catch (NoSuchElementException e){
            return null;
        }finally {
            if (isMinimumSize(cache)) executorService.submit(this::tryFetchAndUpdateCache);
        }
    }

    private boolean isMinimumSize(Collection<Card> cache){
        return cache.size() < MIN_SIZE;
    }

    private void tryFetchAndUpdateCache(){
        try {
            fetchAndUpdateCache();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fetchAndUpdateCache() throws Exception{
        final List<Card> readyCards = getReadyCards();
        synchronized (this){
            cache.clear();
            cache.addAll(readyCards);
        }
    }
}
