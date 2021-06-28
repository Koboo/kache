package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.events.ReceiveEvent;
import eu.koboo.endpoint.core.util.SharedFutures;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.SharedCacheImpl;
import eu.koboo.kache.channel.TransferChannelImpl;
import eu.koboo.kache.packets.cache.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.cache.server.ServerResolveManyPacket;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static eu.koboo.endpoint.core.util.SwitchClass.ccase;
import static eu.koboo.endpoint.core.util.SwitchClass.cswitch;

public class KacheClientListener implements Consumer<ReceiveEvent> {

    final KacheClient client;

    public KacheClientListener(KacheClient client) {
        this.client = client;
    }

    @Override
    public void accept(ReceiveEvent event) {
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
}
