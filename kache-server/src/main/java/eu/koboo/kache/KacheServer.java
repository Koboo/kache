package eu.koboo.kache;

import eu.koboo.endpoint.server.EndpointServer;
import eu.koboo.kache.map.CacheMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class KacheServer extends EndpointServer {

    private final Map<String, CacheMap<String, byte[]>> serverCache = new ConcurrentHashMap<>();

    public KacheServer() {
        this(-1);
    }

    public KacheServer(int port) {
        super(Kache.ENDPOINT_BUILDER, port);
        eventHandler().register(new CacheServerListener(this));
    }

    public List<String> getAllKeys(String name) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        return cacheMap != null && !cacheMap.isEmpty() ? new ArrayList<>(cacheMap.keySet()) : new ArrayList<>();
    }

    public void cache(String name, Map<String, byte[]> mapToCache) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if (cacheMap == null)
            cacheMap = new CacheMap<>(TimeUnit.SECONDS.toMillis(5));
        for (Map.Entry<String, byte[]> entry : mapToCache.entrySet()) {
            cacheMap.put(entry.getKey(), entry.getValue());
        }
        if (!serverCache.containsKey(name))
            serverCache.put(name, cacheMap);
    }

    public Map<String, Boolean> exists(String name, List<String> listToContains) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        Map<String, Boolean> containsMap = new HashMap<>();
        if (cacheMap == null) {
            for (String key : listToContains) {
                containsMap.put(key, false);
            }
        } else {
            for (String key : listToContains) {
                containsMap.put(key, cacheMap.containsKey(key));
            }
        }
        return containsMap;
    }

    public void invalidate(String name, List<String> listToInvalidate) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if (cacheMap != null) {
            for (String key : listToInvalidate) {
                cacheMap.remove(key);
            }
            if (cacheMap.isEmpty())
                serverCache.remove(name);
        }
    }

    public Map<String, byte[]> resolve(String name, List<String> listToResolve) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        Map<String, byte[]> resolveMap = new HashMap<>();
        if (cacheMap != null) {
            for (String key : listToResolve) {
                if (cacheMap.containsKey(key))
                    resolveMap.put(key, cacheMap.get(key));
            }
        }
        return resolveMap;
    }
}
