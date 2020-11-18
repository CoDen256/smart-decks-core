package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.persistence.Database;
import coden.cards.reminder.BaseReminder;
import coden.cards.user.User;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CachedCardModel extends CardModelImpl{

    public static final int MIN_SIZE = 2;

    private final Deque<Card> cache = new LinkedList<>();

    private CompletableFuture<Deque<Card>> newCache;

    public CachedCardModel(User user, BaseReminder reminder, Database database, int pollMinutes) {
        super(user, reminder, database);
        updateNewCache();
        runScheduler(pollMinutes, pollMinutes);
    }

    private void runScheduler(int delay, int period) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateNewCache, delay, period, TimeUnit.MINUTES);
    }

    /**
     * Returns a next card. Two possible cases can occur:
     * 1) Cache is empty or new cache is done:
     * Take newCache future and update cache with it (on complete)
     *
     * 2) Cache is not empty:
     * Wrap existing cache with completable future
     *
     * Ultimately:
     * Check if cache's size is minimum
     *  - if true assign to newCache a new future (if previous future was done)
     *  - if false do nothing
     * Pop last element (never null, because is not empty)
     * @return the next element from ready cards
     */
    @Override
    public CompletableFuture<Card> getNextCard() {
        CompletableFuture<Deque<Card>> cacheToQuery;
        // 1)popped from cache, new cache was not updated
        // should go for own cache
        // 2) first run, newCache is done, but cache is empty, should go for newCache
        // 3) cache is not empty, but newCache is Done()
        if (cache.isEmpty()){
            cacheToQuery = newCache.thenApply(this::updateCacheWithNew);
        }
        else {
            cacheToQuery = createCompletableFuture(cache);
        }
        return cacheToQuery
                .thenApply(this::updateNewCacheOnMinimum)
                .thenApply(this::popEmptyToNull);

    }


    private Deque<Card> updateNewCacheOnMinimum(Deque<Card> cache){
        if (isMinimumSize(cache)){
            updateNewCache();
        }
        return cache;
    }

    private void updateNewCache(){
        if (newCache == null || newCache.isDone())
            newCache = createNewCache();
    }

    private Deque<Card> updateCacheWithNew(Deque<Card> newCache){
        cache.clear();
        cache.addAll(newCache);
        return cache;
    }

    private Card popEmptyToNull(Deque<Card> deque){
        try {
            return deque.pop();
        }catch (NoSuchElementException e){
            return null;
        }
    }

    private CompletableFuture<Deque<Card>> createNewCache() {
        return getReadyCards().thenApply(LinkedList::new);
    }

    private <T> CompletableFuture<T> createCompletableFuture(T value){
        final CompletableFuture<T> cardFuture = new CompletableFuture<T>();
        cardFuture.complete(value);
        return cardFuture;
    }

    private boolean isMinimumSize(Deque<Card> cache){
        return cache.size() < MIN_SIZE;
    }


}
