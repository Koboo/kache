package eu.koboo.kache.cache;

import eu.koboo.endpoint.core.util.SharedFutures;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.result.ExistsManyResult;
import eu.koboo.kache.cache.result.ExistsResult;
import eu.koboo.kache.cache.result.ResolveManyResult;
import eu.koboo.kache.cache.result.ResolveResult;
import eu.koboo.kache.packets.cache.client.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SharedCacheImpl<V extends Serializable> implements SharedCache<V> {

    final String cacheName;
    final KacheClient client;

    public SharedCacheImpl(String cacheName, KacheClient client) {
        this.cacheName = cacheName.toLowerCase(Locale.ROOT);
        this.client = client;
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    private CompletableFuture<Map<String, Boolean>> existsFuture(List<String> listToExists) {
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
            client.send(packet);
        } catch (Exception e) {
            client.onException(getClass(), e);
            future.complete(new HashMap<>());
        }
        return future;
    }

    @Override
    public ExistsManyResult existsMany(List<String> listToExists) {
        CompletableFuture<Map<String, Boolean>> future = existsFuture(listToExists);
        return new ExistsManyResult(future);
    }

    @Override
    public ExistsResult exists(String id) {
        List<String> listToExists = new ArrayList<>();
        listToExists.add(id);
        CompletableFuture<Map<String, Boolean>> future = existsFuture(listToExists);
        return new ExistsResult(id, future);
    }

    @Override
    public void pushMany(Map<String, V> mapToCache) {
        if (mapToCache == null || mapToCache.isEmpty())
            return;
        Map<String, byte[]> cacheMap = new HashMap<>();
        for (Map.Entry<String, V> entry : mapToCache.entrySet()) {
            byte[] valueBytes = client.getSerializerPool().serialize(entry.getValue());
            cacheMap.put(entry.getKey(), valueBytes);
        }
        ClientPushManyPacket packet = new ClientPushManyPacket();
        packet.setCacheName(cacheName);
        packet.setMapToCache(cacheMap);
        client.send(packet);
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
        client.send(packet);
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
        client.send(packet);
    }

    private CompletableFuture<Map<String, V>> resolveFuture(List<String> listToResolve) {
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
            client.send(packet);
            return future;
        } catch (Exception e) {
            client.onException(getClass(), e);
            future.complete(new HashMap<>());
        }
        return future;
    }

    @Override
    public ResolveManyResult<V> resolveMany(List<String> listToResolve) {
        CompletableFuture<Map<String, V>> future = resolveFuture(listToResolve);
        return new ResolveManyResult<>(future);
    }

    @Override
    public ResolveResult<V> resolve(String id) {
        List<String> listToResolve = new ArrayList<>();
        listToResolve.add(id);
        CompletableFuture<Map<String, V>> future = resolveFuture(listToResolve);
        return new ResolveResult<>(id, future);
    }

    @Override
    public ResolveManyResult<V> resolveAll() {
        Map.Entry<String, CompletableFuture<Map<String, V>>> futureEntry = SharedFutures.generateFuture();
        CompletableFuture<Map<String, V>> future = futureEntry.getValue();
        try {
            ClientResolveAllPacket packet = new ClientResolveAllPacket();
            packet.setFutureId(futureEntry.getKey());
            packet.setCacheName(cacheName);
            client.send(packet);
        } catch (Exception e) {
            client.onException(getClass(), e);
            future.complete(new HashMap<>());
        }
        return new ResolveManyResult<>(future);
    }

    @Override
    public void forceMany(Map<String, Boolean> mapToForce) {
        if (mapToForce == null || mapToForce.isEmpty())
            return;
        ClientForceManyPacket packet = new ClientForceManyPacket();
        packet.setCacheName(cacheName);
        packet.setForceMap(mapToForce);
        client.send(packet);
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
        client.send(packet);
    }

    public void completeResolveFuture(String futureId, Map<String, byte[]> resolvedMap) {
        CompletableFuture<Map<String, V>> future = SharedFutures.getFuture(futureId);

        Map<String, V> serializedMap = new HashMap<>();
        if (resolvedMap != null && !resolvedMap.isEmpty()) {
            for (Map.Entry<String, byte[]> entry : resolvedMap.entrySet()) {
                V value = client.getSerializerPool().deserialize(entry.getValue());
                serializedMap.put(entry.getKey(), value);
            }
        }
        future.complete(serializedMap);
    }
}
