package eu.koboo.kache.packets.cache.server;

import eu.koboo.endpoint.core.util.BufUtils;
import eu.koboo.kache.packets.cache.CachePacket;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class ServerResolveManyPacket extends CachePacket {

    private String futureId;
    private Map<String, byte[]> mapToResolve;

    public ServerResolveManyPacket() {
    }

    public String getFutureId() {
        return futureId;
    }

    public void setFutureId(String futureId) {
        this.futureId = futureId;
    }

    public Map<String, byte[]> getMapToResolve() {
        return mapToResolve;
    }

    public void setMapToResolve(Map<String, byte[]> mapToResolve) {
        this.mapToResolve = mapToResolve;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        futureId = BufUtils.readString(byteBuf);
        cacheName = BufUtils.readString(byteBuf);
        int size = BufUtils.readVarInt(byteBuf);
        mapToResolve = new HashMap<>();
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            byte[] valueBytes = BufUtils.readArray(byteBuf);
            mapToResolve.put(key, valueBytes);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(futureId, byteBuf);
        BufUtils.writeString(cacheName, byteBuf);
        int size = mapToResolve.size();
        BufUtils.writeVarInt(size, byteBuf);
        for(Map.Entry<String, byte[]> entry : mapToResolve.entrySet()) {
            String identifier = entry.getKey();
            BufUtils.writeString(identifier, byteBuf);
            BufUtils.writeArray(entry.getValue(), byteBuf);
        }
    }
}
