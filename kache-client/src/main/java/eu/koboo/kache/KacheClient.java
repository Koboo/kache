package eu.koboo.kache;

import eu.koboo.endpoint.client.EndpointClient;
import eu.koboo.kache.cache.LocalCache;
import eu.koboo.kache.cache.LocalCacheImpl;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KacheClient extends EndpointClient {

    private final Map<String, LocalCacheImpl> clientCache = new ConcurrentHashMap<>();

    public KacheClient() {
        this(null, -1);
    }

    public KacheClient(String host, int port) {
        super(Kache.ENDPOINT_BUILDER, host, port);
        eventHandler().register(new CacheClientListener(this));
    }

    public <V extends Serializable> LocalCache<V> getCache(String name) {
        name = name.toLowerCase(Locale.ROOT);
        LocalCacheImpl<V> localCache = clientCache.get(name);
        if(localCache == null)
            localCache = new LocalCacheImpl<>(name, this);
        return localCache;
    }



}
