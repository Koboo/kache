package eu.koboo.kache.cache.local;

import eu.koboo.kache.cache.Cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface LocalCache<V extends Serializable> extends Cache<V> {

    Map<String, Boolean> existsMany(List<String> id);

    boolean exists(String id);

    Map<String, V> resolveMany(List<String> listToResolve);

    V resolve(String id);

    Map<String, V> resolveAll();

}
