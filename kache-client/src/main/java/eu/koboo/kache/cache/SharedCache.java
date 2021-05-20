package eu.koboo.kache.cache;

import eu.koboo.kache.cache.result.ExistsManyResult;
import eu.koboo.kache.cache.result.ExistsResult;
import eu.koboo.kache.cache.result.ResolveManyResult;
import eu.koboo.kache.cache.result.ResolveResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SharedCache<V extends Serializable> {

    String getCacheName();

    ExistsManyResult existsMany(List<String> id);

    ExistsResult exists(String id);

    ResolveManyResult<V> resolveMany(List<String> listToResolve);

    ResolveResult<V> resolve(String id);

    ResolveManyResult<V> resolveAll();

    void pushMany(Map<String, V> mapToCache);

    void push(String id, V value);

    void invalidateMany(List<String> listToInvalidate);

    void invalidate(String id);

    void invalidateAll();

    void forceMany(Map<String, Boolean> id);

    void force(String id, boolean force);

    void timeToLive(long timeToLive);

}
