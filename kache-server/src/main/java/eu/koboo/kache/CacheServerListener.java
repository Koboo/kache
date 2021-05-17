package eu.koboo.kache;

import eu.koboo.endpoint.core.protocols.natives.NativeReceiveEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.event.listener.EventPriority;
import eu.koboo.kache.packets.client.*;
import eu.koboo.kache.packets.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.server.ServerResolveManyPacket;

import java.util.HashMap;
import java.util.Map;

import static eu.koboo.nettyutils.SwitchClass.ccase;
import static eu.koboo.nettyutils.SwitchClass.cswitch;

public class CacheServerListener extends EventListener<NativeReceiveEvent> {

    final KacheServer server;

    public CacheServerListener(KacheServer server) {
        this.server = server;
    }

    @Override
    public void onEvent(NativeReceiveEvent event) {
        cswitch(event.getTypeObject(),
                ccase(ClientPushManyPacket.class, p -> {
                    if (!p.getMapToCache().isEmpty())
                        server.cache(p.getCacheName(), p.getMapToCache());
                }),
                ccase(ClientExistsManyPacket.class, p -> {

                    ServerExistsManyPacket packet = new ServerExistsManyPacket();
                    packet.setFutureId(p.getFutureId());
                    packet.setCacheName(p.getCacheName());

                    Map<String, Boolean> existsMap = new HashMap<>();
                    if (!p.getListToContains().isEmpty())
                        existsMap = server.exists(p.getCacheName(), p.getListToContains());
                    packet.setMapToContains(existsMap);

                    event.getChannel().writeAndFlush(packet);
                }),
                ccase(ClientInvalidateAllPacket.class, p -> {
                    server.invalidate(p.getCacheName(), server.getAllKeys(p.getCacheName()));
                }),
                ccase(ClientInvalidateManyPacket.class, p -> {
                    server.invalidate(p.getCacheName(), p.getListToInvalidate());
                }),
                ccase(ClientResolveAllPacket.class, p -> {

                    ServerResolveManyPacket packet = new ServerResolveManyPacket();
                    packet.setFutureId(p.getFutureId());
                    packet.setCacheName(p.getCacheName());

                    Map<String, byte[]> resolveMap = server.resolve(p.getCacheName(), server.getAllKeys(p.getCacheName()));
                    packet.setMapToResolve(resolveMap);

                    event.getChannel().writeAndFlush(packet);

                }),
                ccase(ClientResolveManyPacket.class, p -> {

                    System.out.println("ResolveMany!");

                    ServerResolveManyPacket packet = new ServerResolveManyPacket();
                    packet.setFutureId(p.getFutureId());
                    packet.setCacheName(p.getCacheName());

                    Map<String, byte[]> resolveMap = new HashMap<>();
                    if (!p.getListToResolve().isEmpty())
                        resolveMap = server.resolve(p.getCacheName(), p.getListToResolve());
                    packet.setMapToResolve(resolveMap);

                    event.getChannel().writeAndFlush(packet);
                }));
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.HIGHEST;
    }
}
