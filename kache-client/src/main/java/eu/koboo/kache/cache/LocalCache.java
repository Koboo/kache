package eu.koboo.kache.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface LocalCache<V extends Serializable> {

    Map<String, Boolean> existsMany(List<String> id);

    boolean exists(String id);

    void pushMany(Map<String, V> mapToCache);

    void push(String id, V value);

    void invalidateMany(List<String> listToInvalidate);

    void invalidate(String id);

    void invalidateAll();

    Map<String, V> resolveMany(List<String> listToResolve);

    V resolve(String id);

    Map<String, V> resolveAll();

    void forceMany(Map<String, Boolean> id);

    void force(String id, boolean force);

    void cacheTime(long cacheTime);

}
