package eu.koboo.kache;

import eu.koboo.endpoint.client.EndpointClient;
import eu.koboo.kache.cache.future.SharedCache;
import eu.koboo.kache.cache.future.SharedCacheImpl;
import eu.koboo.kache.listener.KacheClientListener;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KacheClient extends EndpointClient {

    private final Map<String, SharedCache<?>> futureCacheMap = new ConcurrentHashMap<>();


    public KacheClient() {
        this(null, -1);
    }

    public KacheClient(String host, int port) {
        super(Kache.ENDPOINT_BUILDER, host, port);
        eventHandler().register(new KacheClientListener(this));
    }

    public <C extends SharedCache<V>, V extends Serializable> C getCache(String name) {
        name = name.toLowerCase(Locale.ROOT);
        SharedCache<V> localCache = (SharedCache<V>) futureCacheMap.get(name);
        if (localCache == null) {
            localCache = new SharedCacheImpl<>(name, this);
            futureCacheMap.put(name, localCache);
        }
        return (C) localCache;
    }

}
