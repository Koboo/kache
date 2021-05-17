package eu.koboo.kache.events;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;
import eu.koboo.event.CallableEvent;

public class KacheRequestEvent implements CallableEvent {

    final Class<? extends NativePacket> packetClass;
    final String channelId;
    final String cacheName;
    final long processTime;

    public KacheRequestEvent(Class<? extends NativePacket> packetClass, String channelId, String cacheName, long processTime) {
        this.packetClass = packetClass;
        this.channelId = channelId;
        this.cacheName = cacheName;
        this.processTime = processTime;
    }

    public Class<? extends NativePacket> getPacketClass() {
        return packetClass;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getCacheName() {
        return cacheName;
    }

    public long getProcessTime() {
        return processTime;
    }
}
