package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.protocols.natives.NativeReceiveEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.event.listener.EventPriority;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.events.KacheRequestEvent;
import eu.koboo.kache.packets.CachePacket;
import eu.koboo.kache.packets.client.*;
import eu.koboo.kache.packets.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.server.ServerResolveManyPacket;

import java.util.HashMap;
import java.util.Map;

import static eu.koboo.nettyutils.SwitchClass.ccase;
import static eu.koboo.nettyutils.SwitchClass.cswitch;

public class KacheServerListener extends EventListener<NativeReceiveEvent> {

    final KacheServer server;

    public KacheServerListener(KacheServer server) {
        this.server = server;
    }

    @Override
    public void onEvent(NativeReceiveEvent event) {
        long start = System.nanoTime();
        cswitch(event.getTypeObject(),
                ccase(ClientTimeToLivePacket.class, p -> {
                    server.cacheTime(p.getCacheName(), p.getCacheTimeMillis());
                }),
                ccase(ClientPushManyPacket.class, p -> {
                    if (!p.getMapToCache().isEmpty())
                        server.push(p.getCacheName(), p.getMapToCache());
                }),
                ccase(ClientForceManyPacket.class, p -> {
                    if (!p.getForceMap().isEmpty())
                        server.force(p.getCacheName(), p.getForceMap());
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

                    ServerResolveManyPacket packet = new ServerResolveManyPacket();
                    packet.setFutureId(p.getFutureId());
                    packet.setCacheName(p.getCacheName());

                    Map<String, byte[]> resolveMap = new HashMap<>();
                    if (!p.getListToResolve().isEmpty())
                        resolveMap = server.resolve(p.getCacheName(), p.getListToResolve());
                    packet.setMapToResolve(resolveMap);

                    event.getChannel().writeAndFlush(packet);
                }));
        long end = System.nanoTime();
        String cacheName = "{invalid}";
        if(event.getTypeObject() instanceof CachePacket) {
            cacheName = ((CachePacket) event.getTypeObject()).getCacheName();
        }
        server.eventHandler().callEvent(new KacheRequestEvent(event.getTypeObject().getClass(), event.getChannel().id().toString(), cacheName, end - start));
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.HIGHEST;
    }
}
