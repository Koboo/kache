package eu.koboo.kache.cache;

import eu.koboo.kache.Kache;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.packets.client.*;
import eu.koboo.nettyutils.SharedFutures;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class LocalCacheImpl<V extends Serializable> implements LocalCache<V> {

    final String cacheName;
    final KacheClient client;

    public LocalCacheImpl(String cacheName, KacheClient client) {
        this.cacheName = cacheName.toLowerCase(Locale.ROOT);
        this.client = client;
    }

    @Override
    public Map<String, Boolean> existsMany(List<String> listToExists) {
        try {
            if (listToExists == null || listToExists.isEmpty())
                return new HashMap<>();
            Map.Entry<String, CompletableFuture<Map<String, Boolean>>> futureEntry = SharedFutures.generateFuture();
            ClientExistsManyPacket packet = new ClientExistsManyPacket();
            packet.setFutureId(futureEntry.getKey());
            packet.setCacheName(cacheName);
            packet.setListToContains(listToExists);
            client.send(packet, false);
            return futureEntry.getValue().get();
        } catch (Exception e) {
            client.onException(getClass(), e);
        }
        return new HashMap<>();
    }

    @Override
    public boolean exists(String id) {
        List<String> listToExists = new ArrayList<>();
        listToExists.add(id);
        Map<String, Boolean> existsMap = existsMany(listToExists);
        return existsMap != null && !existsMap.isEmpty() && existsMap.containsKey(id) && existsMap.get(id);
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
    public Map<String, V> resolveMany(List<String> listToResolve) {
        try {
            if (listToResolve == null || listToResolve.isEmpty())
                return new HashMap<>();

            Map.Entry<String, CompletableFuture<Map<String, V>>> futureEntry = SharedFutures.generateFuture();
            ClientResolveManyPacket packet = new ClientResolveManyPacket();
            packet.setFutureId(futureEntry.getKey());
            packet.setCacheName(cacheName);
            packet.setListToResolve(listToResolve);
            client.send(packet, false);
            return futureEntry.getValue().get(listToResolve.size() * 100L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            client.onException(getClass(), e);
        }
        return new HashMap<>();
    }

    @Override
    public V resolve(String id) {
        List<String> listToResolve = new ArrayList<>();
        listToResolve.add(id);
        Map<String, V> resolveMap = resolveMany(listToResolve);

        return resolveMap != null && !resolveMap.isEmpty() && resolveMap.containsKey(id) ? resolveMap.get(id) : null;
    }

    @Override
    public Map<String, V> resolveAll() {
        try {
            Map.Entry<String, CompletableFuture<Map<String, V>>> futureEntry = SharedFutures.generateFuture();
            ClientResolveAllPacket packet = new ClientResolveAllPacket();
            packet.setFutureId(futureEntry.getKey());
            packet.setCacheName(cacheName);
            client.send(packet, false);
            return futureEntry.getValue().get();
        } catch (Exception e) {
            client.onException(getClass(), e);
        }
        return new HashMap<>();
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
