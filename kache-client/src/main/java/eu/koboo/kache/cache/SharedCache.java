package eu.koboo.kache.cache;

import eu.koboo.endpoint.core.util.SharedFutures;
import eu.koboo.endpoint.transferable.Transferable;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.result.ExistsManyResult;
import eu.koboo.kache.cache.result.ExistsResult;
import eu.koboo.kache.cache.result.ResolveManyResult;
import eu.koboo.kache.cache.result.ResolveResult;
import eu.koboo.kache.packets.cache.client.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SharedCache<V extends Transferable> {

    final String cacheName;
    final KacheClient client;

    public SharedCache(String cacheName, KacheClient client) {
        this.cacheName = cacheName.toLowerCase(Locale.ROOT);
        this.client = client;
    }

    public String getCacheName() {
        return cacheName;
    }

    public ExistsManyResult existsMany(List<String> listToExists) {
        CompletableFuture<Map<String, Boolean>> future = existsFuture(listToExists);
        return new ExistsManyResult(future);
    }

    public ExistsResult exists(String id) {
        List<String> listToExists = new ArrayList<>();
        listToExists.add(id);
        CompletableFuture<Map<String, Boolean>> future = existsFuture(listToExists);
        return new ExistsResult(id, future);
    }

    public void pushMany(Map<String, V> mapToCache) {
        if (mapToCache == null || mapToCache.isEmpty())
            return;
        Map<String, byte[]> cacheMap = new HashMap<>();
        for (Map.Entry<String, V> entry : mapToCache.entrySet()) {
            byte[] valueBytes = client.getTransferCodec().encode(entry.getValue());
            cacheMap.put(entry.getKey(), valueBytes);
        }
        ClientPushManyPacket packet = new ClientPushManyPacket();
        packet.setCacheName(cacheName);
        packet.setMapToCache(cacheMap);
        client.send(packet);
    }

    public void push(String id, V value) {
        Map<String, V> mapToCache = new HashMap<>();
        mapToCache.put(id, value);
        pushMany(mapToCache);
    }

    public void invalidateMany(List<String> listToInvalidate) {
        if (listToInvalidate == null || listToInvalidate.isEmpty())
            return;
        ClientInvalidateManyPacket packet = new ClientInvalidateManyPacket();
        packet.setCacheName(cacheName);
        packet.setListToInvalidate(listToInvalidate);
        client.send(packet);
    }

    public void invalidate(String id) {
        List<String> listToInvalidate = new ArrayList<>();
        listToInvalidate.add(id);
        invalidateMany(listToInvalidate);
    }

    public void invalidateAll() {
        ClientInvalidateAllPacket packet = new ClientInvalidateAllPacket();
        packet.setCacheName(cacheName);
        client.send(packet);
    }

    public ResolveManyResult<V> resolveMany(List<String> listToResolve) {
        CompletableFuture<Map<String, V>> future = resolveFuture(listToResolve);
        return new ResolveManyResult<>(future);
    }

    public ResolveResult<V> resolve(String id) {
        List<String> listToResolve = new ArrayList<>();
        listToResolve.add(id);
        CompletableFuture<Map<String, V>> future = resolveFuture(listToResolve);
        return new ResolveResult<>(id, future);
    }

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

    public void forceMany(Map<String, Boolean> mapToForce) {
        if (mapToForce == null || mapToForce.isEmpty())
            return;
        ClientForceManyPacket packet = new ClientForceManyPacket();
        packet.setCacheName(cacheName);
        packet.setForceMap(mapToForce);
        client.send(packet);
    }

    public void force(String id, boolean force) {
        Map<String, Boolean> forceMap = new HashMap<>();
        forceMap.put(id, force);
        forceMany(forceMap);
    }

    public void timeToLive(long timeToLive) {
        ClientTimeToLivePacket packet = new ClientTimeToLivePacket();
        packet.setCacheName(cacheName);
        packet.setCacheTimeMillis(timeToLive);
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
}
