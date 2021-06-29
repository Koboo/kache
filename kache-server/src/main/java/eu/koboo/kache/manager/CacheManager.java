package eu.koboo.kache.manager;

import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.map.CacheMap;
import eu.koboo.kache.packets.cache.client.*;
import eu.koboo.kache.packets.cache.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.cache.server.ServerResolveManyPacket;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheManager {

    private final KacheServer server;
    private final Map<String, CacheMap<String, byte[]>> serverCache = new ConcurrentHashMap<>();

    public CacheManager(KacheServer server) {
        this.server = server;
    }

    public List<String> getAllKeys(String name) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        return cacheMap != null && !cacheMap.isEmpty() ? new ArrayList<>(cacheMap.keySet()) : new ArrayList<>();
    }

    public void push(ClientPushManyPacket packet) {
        if(!packet.getMapToCache().isEmpty()) {
            String name = packet.getCacheName().toLowerCase(Locale.ROOT);
            CacheMap<String, byte[]> cacheMap = serverCache.get(name);
            if (cacheMap == null) {
                cacheMap = new CacheMap<>(TimeUnit.SECONDS.toMillis(30));
                server.fireEvent(new LogEvent("Creating new cache-map '" + name + "' (push)"));
            }
            for (Map.Entry<String, byte[]> entry : packet.getMapToCache().entrySet()) {
                cacheMap.remove(entry.getKey());
                cacheMap.put(entry.getKey(), entry.getValue());
            }
            if (!serverCache.containsKey(name))
                serverCache.put(name, cacheMap);
        }
    }

    public void exists(Channel channel, ClientExistsManyPacket packet) {

        ServerExistsManyPacket response = new ServerExistsManyPacket();
        response.setFutureId(packet.getFutureId());
        response.setCacheName(packet.getCacheName());

        Map<String, Boolean> existsMap = new HashMap<>();
        if (!packet.getListToContains().isEmpty()) {

            String name = packet.getCacheName().toLowerCase(Locale.ROOT);
            CacheMap<String, byte[]> cacheMap = serverCache.get(name);
            Map<String, Boolean> containsMap = new HashMap<>();
            if (cacheMap == null) {
                for (String key : packet.getListToContains()) {
                    existsMap.put(key, false);
                }
            } else {
                for (String key : packet.getListToContains()) {
                    existsMap.put(key, cacheMap.containsKey(key));
                }
            }

        }
        response.setMapToContains(existsMap);
        channel.writeAndFlush(response);
    }

    public void invalidate(ClientInvalidateManyPacket packet) {
        if(!packet.getListToInvalidate().isEmpty()) {
            String name = packet.getCacheName().toLowerCase(Locale.ROOT);
            CacheMap<String, byte[]> cacheMap = serverCache.get(name);
            if (cacheMap != null) {
                for (String key : packet.getListToInvalidate()) {
                    cacheMap.remove(key);
                }
                if (cacheMap.isEmpty())
                    serverCache.remove(name);
            }
        }
    }

    public void invalidateAll(ClientInvalidateAllPacket packet) {
        String name = packet.getCacheName().toLowerCase(Locale.ROOT);
        ClientInvalidateManyPacket invalidatePacket = new ClientInvalidateManyPacket();
        invalidatePacket.setListToInvalidate(getAllKeys(name));
        invalidatePacket.setCacheName(name);
        invalidate(invalidatePacket);
    }

    public void resolve(Channel channel, ClientResolveManyPacket packet) {
        ServerResolveManyPacket response = new ServerResolveManyPacket();
        response.setFutureId(packet.getFutureId());
        response.setCacheName(packet.getCacheName());

        Map<String, byte[]> resolveMap = new HashMap<>();
        if (!packet.getListToResolve().isEmpty()) {
            String name = packet.getCacheName().toLowerCase(Locale.ROOT);
            CacheMap<String, byte[]> cacheMap = serverCache.get(name);
            if (cacheMap != null) {
                for (String key : packet.getListToResolve()) {
                    if (cacheMap.containsKey(key))
                        resolveMap.put(key, cacheMap.get(key));
                }
            }
        }
        response.setMapToResolve(resolveMap);
        channel.writeAndFlush(response);
    }

    public void resolveAll(Channel channel, ClientResolveAllPacket packet) {
        String name = packet.getCacheName().toLowerCase(Locale.ROOT);
        ClientResolveManyPacket manyPacket = new ClientResolveManyPacket();
        manyPacket.setFutureId(packet.getFutureId());
        manyPacket.setCacheName(name);
        manyPacket.setListToResolve(getAllKeys(name));
        resolve(channel, manyPacket);
    }

    public void force(ClientForceManyPacket packet) {
        String name = packet.getCacheName().toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if (cacheMap != null) {
            for (String key : packet.getForceMap().keySet()) {
                if (cacheMap.containsKey(key)) {
                    cacheMap.setForced(key, packet.getForceMap().get(key));
                }
            }
        }
    }

    public void cacheTime(ClientTimeToLivePacket packet) {
        String name = packet.getCacheName().toLowerCase(Locale.ROOT);
        long cacheTime = packet.getCacheTimeMillis();
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if(cacheMap == null) {
            cacheMap = new CacheMap<>(cacheTime);
            serverCache.put(name, cacheMap);
            server.fireEvent(new LogEvent("Creating new cache-map '" + name + "' (timeToLive)"));
        }
        if(cacheMap.getTimeToLive() != cacheTime) {
            cacheMap.setTimeToLive(cacheTime);
        }
    }
}
