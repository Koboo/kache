package eu.koboo.kache.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface LocalCache< V extends Serializable> {

    Map<String, Boolean> existsMany(List<String> id);

    void cacheMany(Map<String, V> mapToCache);

    void invalidateMany(List<String> listToInvalidate);

    void invalidateAll();

    Map<String, V> resolveMany(List<String> listToResolve);

    Map<String, V> resolveAll();

    boolean exists(String id);

    void cache(String id, V value);

    void invalidate(String id);

    V resolve(String id);

}
