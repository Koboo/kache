package eu.koboo.kache;

import eu.binflux.serial.core.SerializerPool;
import eu.binflux.serial.fst.FSTSerialization;
import eu.koboo.endpoint.client.EndpointClient;
import eu.koboo.endpoint.core.events.ReceiveEvent;
import eu.koboo.endpoint.transferable.TransferCodec;
import eu.koboo.endpoint.transferable.Transferable;
import eu.koboo.kache.cache.SharedCache;
import eu.koboo.kache.channel.TransferChannel;
import eu.koboo.kache.listener.KacheClientListener;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KacheClient extends EndpointClient {

    private final Map<String, SharedCache<?>> sharedCacheMap = new ConcurrentHashMap<>();
    private final Map<String, TransferChannel<?>> transferChannelMap = new ConcurrentHashMap<>();
    private final SerializerPool serializerPool;
    private final TransferCodec transferCodec;

    public KacheClient() {
        this(null, -1);
    }

    public KacheClient(String host, int port) {
        super(Kache.ENDPOINT_BUILDER, host, port);
        registerEvent(ReceiveEvent.class, new KacheClientListener(this));
        transferCodec = new TransferCodec();
        serializerPool = new SerializerPool(FSTSerialization.class);
    }

    /*public SerializerPool getSerializerPool() {
        return serializerPool;
    }*/

    public <C extends SharedCache<V>, V extends Transferable> C getCache(String name) {
        name = name.toLowerCase(Locale.ROOT);
        SharedCache<V> localCache = (SharedCache<V>) sharedCacheMap.get(name);
        if (localCache == null) {
            localCache = new SharedCache<>(name, this);
            sharedCacheMap.put(name, localCache);
        }
        return (C) localCache;
    }

    public <T extends TransferChannel<V>, V extends Transferable> T getTransfer(String channel) {
        channel = channel.toLowerCase(Locale.ROOT);
        TransferChannel<V> transferChannel = (TransferChannel<V>) transferChannelMap.get(channel);
        if(transferChannel == null) {
            transferChannel = new TransferChannel<>(this, channel);
            transferChannelMap.put(channel, transferChannel);
        }
        return (T) transferChannel;
    }

    public TransferCodec getTransferCodec() {
        return transferCodec;
    }
}
