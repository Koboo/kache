package eu.koboo.kache;

import eu.koboo.endpoint.client.EndpointClient;
import eu.koboo.kache.cache.future.SharedCache;
import eu.koboo.kache.cache.future.SharedCacheImpl;
import eu.koboo.kache.channel.TransferChannel;
import eu.koboo.kache.channel.TransferChannelImpl;
import eu.koboo.kache.listener.KacheClientListener;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KacheClient extends EndpointClient {

    private final Map<String, SharedCache<?>> sharedCacheMap = new ConcurrentHashMap<>();
    private final Map<String, TransferChannel<?>> transferChannelMap = new ConcurrentHashMap<>();

    public KacheClient() {
        this(null, -1);
    }

    public KacheClient(String host, int port) {
        super(Kache.ENDPOINT_BUILDER, host, port);
        eventHandler().register(new KacheClientListener(this));
    }

    public <C extends SharedCache<V>, V extends Serializable> C getCache(String name) {
        name = name.toLowerCase(Locale.ROOT);
        SharedCache<V> localCache = (SharedCache<V>) sharedCacheMap.get(name);
        if (localCache == null) {
            localCache = new SharedCacheImpl<>(name, this);
            sharedCacheMap.put(name, localCache);
        }
        return (C) localCache;
    }

    public <T extends TransferChannel<V>, V extends Serializable> T getTransfer(String channel) {
        channel = channel.toLowerCase(Locale.ROOT);
        TransferChannel<V> transferChannel = (TransferChannel<V>) transferChannelMap.get(channel);
        if(transferChannel == null) {
            transferChannel = new TransferChannelImpl<>(this, channel);
            transferChannelMap.put(channel, transferChannel);
        }
        return (T) transferChannel;
    }

}
