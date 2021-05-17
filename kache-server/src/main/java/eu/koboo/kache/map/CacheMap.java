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

    private long lifeTime;

    public CacheMap(long lifeTimeInMillis) {
        this.lifeTime = lifeTimeInMillis;
        CACHE_TIMER.schedule(new CacheValidation<>(this), 0, TimeUnit.SECONDS.toMillis(30));
    }

    protected long getLifeTime() {
        return lifeTime;
    }

    protected Map<K, CacheData> getDataMap() {
        return dataMap;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public boolean isForced(K key) {
        CacheData data = dataMap.get(key);
        return data != null && data.isForced();
    }

    public void setForced(K key, boolean forced) {
        dataMap.remove(key);
        dataMap.put(key, new CacheData(System.currentTimeMillis(), forced));
    }

    public V put(K key, V value, boolean forced) {
        put(key, value);
        setForced(key, forced);
        return value;
    }

    @Override
    public V put(K key, V value) {
        dataMap.remove(key);
        dataMap.put(key, new CacheData(System.currentTimeMillis(), isForced(key)));
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