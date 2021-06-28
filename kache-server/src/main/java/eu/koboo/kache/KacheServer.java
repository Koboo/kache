package eu.koboo.kache;

import eu.koboo.endpoint.core.builder.param.ErrorMode;
import eu.koboo.endpoint.core.events.ReceiveEvent;
import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.endpoint.server.EndpointServer;
import eu.koboo.kache.listener.KacheServerListener;
import eu.koboo.kache.map.CacheMap;
import eu.koboo.kache.packets.cache.client.ClientForceManyPacket;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class KacheServer extends EndpointServer {

    private final Map<String, CacheMap<String, byte[]>> serverCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> serverChannelRegistry = new ConcurrentHashMap<>();

    public KacheServer() {
        this(!Epoll.isAvailable() ? 6565 : -1);
    }

    public KacheServer(int port) {
        super(Kache.ENDPOINT_BUILDER.errorMode(ErrorMode.EVENT), port);
        registerEvent(ReceiveEvent.class, new KacheServerListener(this));
    }

    public void registerTransfer(Channel channel, String transferChannel) {
        String id = channel.id().toString();
        List<String> channelList = serverChannelRegistry.getOrDefault(id, new ArrayList<>());
        if (!channelList.contains(transferChannel)) {
            channelList.add(transferChannel);
            if(!serverChannelRegistry.containsKey(id))
                serverChannelRegistry.put(id, channelList);
            fireEvent(new LogEvent(id + " > Registered transfer-channel '" + transferChannel + "'"));
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
        serverChannelRegistry.remove(channel.id().toString());
    }

    public List<String> getAllKeys(String name) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        return cacheMap != null && !cacheMap.isEmpty() ? new ArrayList<>(cacheMap.keySet()) : new ArrayList<>();
    }

    public void push(String name, Map<String, byte[]> mapToCache) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if (cacheMap == null) {
            cacheMap = new CacheMap<>(TimeUnit.SECONDS.toMillis(30));
            fireEvent(new LogEvent("Creating new cache-map '" + name + "' (push)"));
        }
        for (Map.Entry<String, byte[]> entry : mapToCache.entrySet()) {
            cacheMap.remove(entry.getKey());
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

    public void cacheTime(String name, long cacheTime) {
        name = name.toLowerCase(Locale.ROOT);
        CacheMap<String, byte[]> cacheMap = serverCache.get(name);
        if(cacheMap == null) {
            cacheMap = new CacheMap<>(cacheTime);
            serverCache.put(name, cacheMap);
            fireEvent(new LogEvent("Creating new cache-map '" + name + "' (timeToLive)"));
        }
        if(cacheMap.getTimeToLive() != cacheTime) {
            cacheMap.setTimeToLive(cacheTime);
        }
    }

}
