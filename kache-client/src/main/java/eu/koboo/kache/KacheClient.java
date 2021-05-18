package eu.koboo.kache;

import eu.koboo.endpoint.client.EndpointClient;
import eu.koboo.kache.cache.Cache;
import eu.koboo.kache.cache.CacheType;
import eu.koboo.kache.cache.local.LocalCache;
import eu.koboo.kache.cache.local.LocalCacheImpl;
import eu.koboo.kache.cache.future.FutureCache;
import eu.koboo.kache.cache.future.FutureCacheImpl;
import eu.koboo.kache.listener.KacheClientListener;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KacheClient extends EndpointClient {

    private final Map<String, LocalCache<?>> localCacheMap = new ConcurrentHashMap<>();
    private final Map<String, FutureCache<?>> futureCacheMap = new ConcurrentHashMap<>();

    public KacheClient() {
        this(null, -1);
    }

    public KacheClient(String host, int port) {
        super(Kache.ENDPOINT_BUILDER, host, port);
        eventHandler().register(new KacheClientListener(this));
    }

    public <C extends Cache<V>, V extends Serializable> C getCache(String name) {
        return getCache(name, CacheType.LOCAL);
    }

    public <C extends Cache<V>, V extends Serializable> C getCache(String name, CacheType cacheType) {
        name = name.toLowerCase(Locale.ROOT);
        switch (cacheType) {
            case LOCAL:
                LocalCache<V> localCache = (LocalCache<V>) localCacheMap.get(name);
                if(localCache == null) {
                    localCache = new LocalCacheImpl<>(name, this);
                    localCacheMap.put(name, localCache);
                }
                return (C) localCache;
            case FUTURE:
                FutureCache<V> futureCache = (FutureCache<V>) localCacheMap.get(name);
                if(futureCache == null) {
                    futureCache = new FutureCacheImpl<>(name, this);
                    futureCacheMap.put(name, futureCache);
                }
                return (C) futureCache;
        }
        return null;
    }

}
