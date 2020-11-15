package coden.cards.model;

import coden.cards.data.Card;
import coden.cards.persistence.Database;
import coden.cards.reminder.BaseReminder;
import coden.cards.user.User;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CachedCardModel extends CardModelImpl{

    public static final int MIN_SIZE = 2;

    private final Deque<Card> cache = new LinkedList<>();

    private CompletableFuture<Deque<Card>> updatingCacheFuture;

    public CachedCardModel(User user, BaseReminder reminder, Database database, int pollMinutes) {
        super(user, reminder, database);
        updateFuture();
        runScheduler(pollMinutes, pollMinutes);
    }

    private void runScheduler(int delay, int period) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateFuture, delay, period, TimeUnit.MINUTES);
    }

    @Override
    public CompletableFuture<Card> getNextCard() {
        try {
            return createCompletableFuture(cache.pop());
        }catch (NoSuchElementException e){
            if (updatingCacheFuture == null) updateFuture();
            return updatingCacheFuture
                    .thenApply(this::popToNull);
        }finally {
            if (isMinimumSize(cache)) updateFuture();
        }
    }

    private Card popToNull(Deque<Card> deque){
        try {
            return deque.pop();
        }catch (NoSuchElementException e){
            return null;
        }
    }

    private void updateFuture(){
        if (updatingCacheFuture == null || updatingCacheFuture.isDone()){
            updatingCacheFuture = createGetAndUpdateCacheFuture();
        }
    }

    private CompletableFuture<Deque<Card>> createGetAndUpdateCacheFuture() {
        try{
            return getReadyCards().thenApply(this::updateCache);
        } catch (Exception ignored){ return null;}
    }

    private CompletableFuture<Card> createCompletableFuture(Card card){
        final CompletableFuture<Card> cardFuture = new CompletableFuture<>();
        cardFuture.complete(card);
        return cardFuture;
    }

    private boolean isMinimumSize(Collection<Card> cache){
        return cache.size() < MIN_SIZE;
    }

    private Deque<Card> updateCache(Collection<Card> cards){
        cache.clear();
        cache.addAll(cards);
        return cache;
    }


}
