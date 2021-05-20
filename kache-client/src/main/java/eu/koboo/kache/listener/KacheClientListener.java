package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.protocols.natives.NativeReceiveEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.event.listener.EventPriority;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.future.SharedCacheImpl;
import eu.koboo.kache.channel.TransferChannelImpl;
import eu.koboo.kache.packets.cache.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.cache.server.ServerResolveManyPacket;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;
import eu.koboo.nettyutils.SharedFutures;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static eu.koboo.nettyutils.SwitchClass.ccase;
import static eu.koboo.nettyutils.SwitchClass.cswitch;

public class KacheClientListener extends EventListener<NativeReceiveEvent> {

    final KacheClient client;

    public KacheClientListener(KacheClient client) {
        this.client = client;
    }

    @Override
    public void onEvent(NativeReceiveEvent event) {
        cswitch(event.getTypeObject(),
                ccase(ServerResolveManyPacket.class, p -> {
                    String futureId = p.getFutureId();
                    SharedCacheImpl<?> localCache = (SharedCacheImpl<?>) client.getCache(p.getCacheName());
                    localCache.completeResolveFuture(futureId, p.getMapToResolve());
                }),
                ccase(ServerExistsManyPacket.class, p -> {
                    String futureId = p.getFutureId();
                    CompletableFuture<Map<String, Boolean>> future = SharedFutures.getFuture(futureId);
                    if(future != null)
                        future.complete(p.getMapToContains());
                }),
                ccase(ServerTransferObjectPacket.class, p -> {
                    TransferChannelImpl<?> transferChannel = (TransferChannelImpl<?>) client.getTransfer(p.getChannel());
                    transferChannel.onReceive(p.getValue());
                }));
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.HIGHEST;
    }
}
