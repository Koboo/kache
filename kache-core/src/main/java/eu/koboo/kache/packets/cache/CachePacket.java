package eu.koboo.kache.packets.cache;

import eu.koboo.endpoint.core.codec.EndpointPacket;

public abstract class CachePacket implements EndpointPacket {

    protected String cacheName;

    public CachePacket() {
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
