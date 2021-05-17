package eu.koboo.kache;

import eu.koboo.endpoint.core.protocols.natives.NativeReceiveEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.event.listener.EventPriority;
import eu.koboo.kache.packets.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.server.ServerResolveManyPacket;
import eu.koboo.nettyutils.SharedFutures;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static eu.koboo.nettyutils.SwitchClass.ccase;
import static eu.koboo.nettyutils.SwitchClass.cswitch;

public class CacheClientListener extends EventListener<NativeReceiveEvent> {

    final KacheClient client;

    public CacheClientListener(KacheClient client) {
        this.client = client;
    }

    @Override
    public void onEvent(NativeReceiveEvent event) {
        cswitch(event.getTypeObject(),
                ccase(ServerResolveManyPacket.class, p -> {
                    String futureId = p.getFutureId();
                    CompletableFuture<Map<String, byte[]>> future = SharedFutures.getFuture(futureId);
                    if(future != null)
                        future.complete(p.getMapToResolve());
                }),
                ccase(ServerExistsManyPacket.class, p -> {
                    String futureId = p.getFutureId();
                    CompletableFuture<Map<String, Boolean>> future = SharedFutures.getFuture(futureId);
                    if(future != null)
                        future.complete(p.getMapToContains());
                }));
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.HIGHEST;
    }
}
