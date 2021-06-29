package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.codec.EndpointPacket;
import eu.koboo.endpoint.core.events.ReceiveEvent;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.events.KacheRequestEvent;
import eu.koboo.kache.packets.cache.CachePacket;
import eu.koboo.kache.packets.cache.client.*;
import eu.koboo.kache.packets.transfer.TransferPacket;
import eu.koboo.kache.packets.transfer.client.ClientRegisterTransferPacket;
import eu.koboo.kache.packets.transfer.client.ClientTransferObjectPacket;

import java.util.function.Consumer;

import static eu.koboo.endpoint.core.util.SwitchClass.ccase;
import static eu.koboo.endpoint.core.util.SwitchClass.cswitch;

public class KacheServerListener implements Consumer<ReceiveEvent> {

    final KacheServer server;

    public KacheServerListener(KacheServer server) {
        this.server = server;
    }

    @Override
    public void accept(ReceiveEvent event) {
        if(event.getTypeObject() instanceof EndpointPacket) {
            EndpointPacket endpointPacket = event.getTypeObject();
            long start = System.nanoTime();
            cswitch(event.getTypeObject(),
                    ccase(ClientTimeToLivePacket.class, server.getCacheManager()::cacheTime),
                    ccase(ClientPushManyPacket.class, server.getCacheManager()::push),
                    ccase(ClientForceManyPacket.class, server.getCacheManager()::force),
                    ccase(ClientExistsManyPacket.class, p -> server.getCacheManager().exists(event.getChannel(), p)),
                    ccase(ClientInvalidateAllPacket.class, server.getCacheManager()::invalidateAll),
                    ccase(ClientInvalidateManyPacket.class, server.getCacheManager()::invalidate),
                    ccase(ClientResolveAllPacket.class, p -> server.getCacheManager().resolveAll(event.getChannel(), p)),
                    ccase(ClientResolveManyPacket.class, p -> server.getCacheManager().resolve(event.getChannel(), p)),
                    ccase(ClientRegisterTransferPacket.class, p -> server.getTransferManager().registerTransfer(event.getChannel(), p)),
                    ccase(ClientTransferObjectPacket.class, p -> server.getTransferManager().transfer(event.getChannel(), p)));
            long end = System.nanoTime();
            String subject = "{invalid}";
            if (event.getTypeObject() instanceof CachePacket) {
                subject = ((CachePacket) event.getTypeObject()).getCacheName();
            } else if (event.getTypeObject() instanceof TransferPacket) {
                subject = ((TransferPacket) event.getTypeObject()).getChannel();
            }
            server.fireEvent(new KacheRequestEvent(endpointPacket.getClass(), event.getChannel().id().toString(), subject, end - start));
        }
    }

}
