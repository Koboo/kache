package eu.koboo.kache.packets.cache.client;

import eu.koboo.kache.packets.cache.CachePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class ClientPushManyPacket extends CachePacket {

    private Map<String, byte[]> mapToCache;

    public ClientPushManyPacket() {
    }

    public Map<String, byte[]> getMapToCache() {
        return mapToCache;
    }

    public void setMapToCache(Map<String, byte[]> mapToCache) {
        this.mapToCache = mapToCache;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        cacheName = BufUtils.readString(byteBuf);
        int size = BufUtils.readVarInt(byteBuf);
        mapToCache = new HashMap<>();
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            byte[] valueBytes = BufUtils.readArray(byteBuf);
            mapToCache.put(key, valueBytes);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(cacheName, byteBuf);
        int size = mapToCache.size();
        BufUtils.writeVarInt(size, byteBuf);
        for(Map.Entry<String, byte[]> entry : mapToCache.entrySet()) {
            BufUtils.writeString(entry.getKey(), byteBuf);
            BufUtils.writeArray(entry.getValue(), byteBuf);
        }
    }
}
