package eu.koboo.kache.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Cache<V extends Serializable> {

    String getCacheName();

    CacheType getType();

    void pushMany(Map<String, V> mapToCache);

    void push(String id, V value);

    void invalidateMany(List<String> listToInvalidate);

    void invalidate(String id);

    void invalidateAll();

    void forceMany(Map<String, Boolean> id);

    void force(String id, boolean force);

    void timeToLive(long timeToLive);
}
