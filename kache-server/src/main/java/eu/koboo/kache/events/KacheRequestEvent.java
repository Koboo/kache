package eu.koboo.kache.events;

import eu.koboo.endpoint.core.codec.EndpointPacket;
import eu.koboo.endpoint.core.events.ConsumerEvent;

public class KacheRequestEvent implements ConsumerEvent {

    final Class<? extends EndpointPacket> packetClass;
    final String channelId;
    final String cacheName;
    final long processTime;

    public KacheRequestEvent(Class<? extends EndpointPacket> packetClass, String channelId, String cacheName, long processTime) {
        this.packetClass = packetClass;
        this.channelId = channelId;
        this.cacheName = cacheName;
        this.processTime = processTime;
    }

    public Class<? extends EndpointPacket> getPacketClass() {
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
