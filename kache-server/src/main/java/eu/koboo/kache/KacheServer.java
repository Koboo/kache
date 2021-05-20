package eu.koboo.kache;

import eu.koboo.endpoint.core.builder.param.ErrorMode;
import eu.koboo.endpoint.server.EndpointServer;
import eu.koboo.kache.listener.KacheServerListener;
import eu.koboo.kache.map.CacheMap;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;
import eu.koboo.nettyutils.NettyType;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class KacheServer extends EndpointServer {

    private final Map<String, CacheMap<String, byte[]>> serverCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> serverChannelRegistry = new ConcurrentHashMap<>();

    public KacheServer() {
        this(!NettyType.prepareType().isEpoll() ? 6565 : -1);
    }

    public KacheServer(int port) {
        super(Kache.ENDPOINT_BUILDER.errorMode(ErrorMode.EVENT), port);
        eventHandler().register(new KacheServerListener(this));
    }

    public void registerTransfer(Channel channel, String transferChannel) {
        String id = channel.id().toString();
        List<String> channelList = serverChannelRegistry.getOrDefault(id, new ArrayList<>());
        if (!channelList.contains(transferChannel)) {
            channelList.add(transferChannel);
            if(!serverChannelRegistry.containsKey(id))
                serverChannelRegistry.put(id, channelList);
        }
    }

    public void handleObjectTransfer(Channel channel, String transferChannel, byte[] value) {
        ServerTransferObjectPacket packet = new ServerTransferObjectPacket();
        packet.setChannel(transferChannel);
        packet.setValue(value);
        for(Channel clients : getChannelGroup()) {
            String id = clients.id().toString();
            if(!id.equals(channel.id().toString()) && hasTransfer(clients, transferChannel)) {
                clients.writeAndFlush(packet);
            }
        }
    }

    public boolean hasTransfer(Channel channel, String transferChannel) {
        String id = channel.id().toString();
        return serverChannelRegistry.getOrDefault(id, new ArrayList<>()).contains(transferChannel);
    }

    public boolean hasAnyTransfer(Channel channel) {
        return serverChannelRegistry.containsKey(channel.id().toString());
    }

    public void clearTransfer(Channel channel) {
        String id = channel.id().toString();
        serverChannelRegistry.remove(id);
    }

    public List<String> getAllKeys(String name) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        return cacheMap != null && !cacheMap.isEmpty() ? new ArrayList<>(cacheMap.keySet()) : new ArrayList<>();
    }

    public void push(String name, Map<String, byte[]> mapToCache) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if (cacheMap == null)
            cacheMap = new CacheMap<>(TimeUnit.SECONDS.toMillis(30));
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

    public void force(String name, Map<String, Boolean> listToForce) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if (cacheMap != null) {
            for (String key : listToForce.keySet()) {
                if (cacheMap.containsKey(key)) {
                    cacheMap.setForced(key, listToForce.get(key));
                }
            }
        }
    }

    public void cacheTime(String name, long cacheTime) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if (cacheMap != null) {
            cacheMap.setTimeToLive(cacheTime);
        }
    }

}
