package eu.koboo.kache.packets.cache;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;

public abstract class CachePacket implements NativePacket {

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
