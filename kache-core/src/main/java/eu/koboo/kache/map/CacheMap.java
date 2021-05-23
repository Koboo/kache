package eu.koboo.kache.map;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {

    private static final Timer CACHE_TIMER = new Timer("CacheTimer");

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(CACHE_TIMER::cancel));
    }

    private final Map<K, CacheData> dataMap = new ConcurrentHashMap<>();

    private long timeToLive;

    public CacheMap(long lifeTimeInMillis) {
        this.timeToLive = lifeTimeInMillis;
        CACHE_TIMER.scheduleAtFixedRate(new CacheValidation<>(this), 0, 500);
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public Map<K, CacheData> getDataMap() {
        return dataMap;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean isForced(K key) {
        CacheData data = dataMap.get(key);
        return data != null && data.isForced();
    }

    public void setForced(K key, boolean forced) {
        dataMap.remove(key);
        dataMap.put(key, new CacheData(System.currentTimeMillis(), forced));
    }

    @Override
    public V put(K key, V value) {
        dataMap.putIfAbsent(key, new CacheData(System.currentTimeMillis(), isForced(key)));
        return super.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        dataMap.putIfAbsent(key, new CacheData(System.currentTimeMillis(), isForced(key)));
        return super.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.dataMap.remove(key);
        return super.remove(key, value);
    }

    @Override
    public V remove(Object key) {
        this.dataMap.remove(key);
        return super.remove(key);
    }
}