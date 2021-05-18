package eu.koboo.kache.cache.future;

import eu.koboo.kache.Kache;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.CacheType;
import eu.koboo.kache.packets.client.*;
import eu.koboo.nettyutils.SharedFutures;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FutureCacheImpl<V extends Serializable> implements FutureCache<V> {

    final String cacheName;
    final KacheClient client;

    public FutureCacheImpl(String cacheName, KacheClient client) {
        this.cacheName = cacheName.toLowerCase(Locale.ROOT);
        this.client = client;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public CacheType getType() {
        return CacheType.FUTURE;
    }

    @Override
    public CompletableFuture<Map<String, Boolean>> existsMany(List<String> listToExists) {
        Map.Entry<String, CompletableFuture<Map<String, Boolean>>> futureEntry = SharedFutures.generateFuture();
        CompletableFuture<Map<String, Boolean>> future = futureEntry.getValue();
        try {
            if (listToExists == null || listToExists.isEmpty()) {
                future.complete(new HashMap<>());
                return future;
            }
            ClientExistsManyPacket packet = new ClientExistsManyPacket();
            packet.setFutureId(futureEntry.getKey());
            packet.setCacheName(cacheName);
            packet.setListToContains(listToExists);
            client.send(packet, false);
            return future;
        } catch (Exception e) {
            client.onException(getClass(), e);
            future.complete(new HashMap<>());
        }
        return future;
    }

    @Override
    public CompletableFuture<Boolean> exists(String id) {
        List<String> listToExists = new ArrayList<>();
        listToExists.add(id);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        existsMany(listToExists)
                .whenComplete((map, e) -> future.complete(map != null && !map.isEmpty() && map.containsKey(id) && map.get(id)));
        return future;
    }

    @Override
    public void pushMany(Map<String, V> mapToCache) {
        if (mapToCache == null || mapToCache.isEmpty())
            return;
        Map<String, byte[]> cacheMap = new HashMap<>();
        for (Map.Entry<String, V> entry : mapToCache.entrySet()) {
            byte[] valueBytes = Kache.ENDPOINT_BUILDER.getSerializerPool().serialize(entry.getValue());
            cacheMap.put(entry.getKey(), valueBytes);
        }
        ClientPushManyPacket packet = new ClientPushManyPacket();
        packet.setCacheName(cacheName);
        packet.setMapToCache(cacheMap);
        client.send(packet, false);
    }

    @Override
    public void push(String id, V value) {
        Map<String, V> mapToCache = new HashMap<>();
        mapToCache.put(id, value);
        pushMany(mapToCache);
    }

    @Override
    public void invalidateMany(List<String> listToInvalidate) {
        if (listToInvalidate == null || listToInvalidate.isEmpty())
            return;
        ClientInvalidateManyPacket packet = new ClientInvalidateManyPacket();
        packet.setCacheName(cacheName);
        packet.setListToInvalidate(listToInvalidate);
        client.send(packet, false);
    }

    @Override
    public void invalidate(String id) {
        List<String> listToInvalidate = new ArrayList<>();
        listToInvalidate.add(id);
        invalidateMany(listToInvalidate);
    }

    @Override
    public void invalidateAll() {
        ClientInvalidateAllPacket packet = new ClientInvalidateAllPacket();
        packet.setCacheName(cacheName);
        client.send(packet, false);
    }

    @Override
    public CompletableFuture<Map<String, V>> resolveMany(List<String> listToResolve) {
        Map.Entry<String, CompletableFuture<Map<String, V>>> futureEntry = SharedFutures.generateFuture();
        CompletableFuture<Map<String, V>> future = futureEntry.getValue();
        try {
            if (listToResolve == null || listToResolve.isEmpty()) {
                future.complete(new HashMap<>());
                return future;
            }
            ClientResolveManyPacket packet = new ClientResolveManyPacket();
            packet.setFutureId(futureEntry.getKey());
            packet.setCacheName(cacheName);
            packet.setListToResolve(listToResolve);
            client.send(packet, false);
            return future;
        } catch (Exception e) {
            client.onException(getClass(), e);
            future.complete(new HashMap<>());
        }
        return future;
    }

    @Override
    public CompletableFuture<V> resolve(String id) {
        List<String> listToResolve = new ArrayList<>();
        listToResolve.add(id);
        CompletableFuture<V> future = new CompletableFuture<>();
        resolveMany(listToResolve).whenComplete((resolveMap, e) -> {
            if(resolveMap != null && !resolveMap.isEmpty() && resolveMap.containsKey(id)) {
                future.complete(resolveMap.get(id));
            } else {
                future.complete(null);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Map<String, V>> resolveAll() {
        Map.Entry<String, CompletableFuture<Map<String, V>>> futureEntry = SharedFutures.generateFuture();
        CompletableFuture<Map<String, V>> future = futureEntry.getValue();
        try {
            ClientResolveAllPacket packet = new ClientResolveAllPacket();
            packet.setFutureId(futureEntry.getKey());
            packet.setCacheName(cacheName);
            client.send(packet, false);
            return future;
        } catch (Exception e) {
            client.onException(getClass(), e);
            future.complete(new HashMap<>());
        }
        return future;
    }

    @Override
    public void forceMany(Map<String, Boolean> mapToForce) {
        if (mapToForce == null || mapToForce.isEmpty())
            return;
        ClientForceManyPacket packet = new ClientForceManyPacket();
        packet.setCacheName(cacheName);
        packet.setForceMap(mapToForce);
        client.send(packet, false);
    }

    @Override
    public void force(String id, boolean force) {
        Map<String, Boolean> forceMap = new HashMap<>();
        forceMap.put(id, force);
        forceMany(forceMap);
    }

    @Override
    public void timeToLive(long timeToLive) {
        ClientTimeToLivePacket packet = new ClientTimeToLivePacket();
        packet.setCacheName(cacheName);
        packet.setCacheTimeMillis(timeToLive);
        client.send(packet, false);
    }

    public void completeResolveFuture(String futureId, Map<String, byte[]> resolvedMap) {
        CompletableFuture<Map<String, V>> future = SharedFutures.getFuture(futureId);

        Map<String, V> serializedMap = new HashMap<>();
        if (resolvedMap != null && !resolvedMap.isEmpty()) {
            for (Map.Entry<String, byte[]> entry : resolvedMap.entrySet()) {
                V value = (V) Kache.ENDPOINT_BUILDER.getSerializerPool().deserialize(entry.getValue());
                serializedMap.put(entry.getKey(), value);
            }
        }
        future.complete(serializedMap);
    }
}
