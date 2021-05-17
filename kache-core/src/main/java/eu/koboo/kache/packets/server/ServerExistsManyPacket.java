package eu.koboo.kache.packets.server;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class ServerExistsManyPacket implements NativePacket {

    private String futureId;
    private String cacheName;
    private Map<String, Boolean> mapToContains;

    public ServerExistsManyPacket() {
    }

    public String getFutureId() {
        return futureId;
    }

    public void setFutureId(String futureId) {
        this.futureId = futureId;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Map<String, Boolean> getMapToContains() {
        return mapToContains;
    }

    public void setMapToContains(Map<String, Boolean> mapToContains) {
        this.mapToContains = mapToContains;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        futureId = BufUtils.readString(byteBuf);
        cacheName = BufUtils.readString(byteBuf);
        int size = BufUtils.readVarInt(byteBuf);
        mapToContains = new HashMap<>();
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            boolean contains = byteBuf.readBoolean();
            mapToContains.put(key, contains);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(futureId, byteBuf);
        BufUtils.writeString(cacheName, byteBuf);
        int size = mapToContains.size();
        BufUtils.writeVarInt(size, byteBuf);
        for(Map.Entry<String, Boolean> entry : mapToContains.entrySet()) {
            BufUtils.writeString(entry.getKey(), byteBuf);
            byteBuf.writeBoolean(entry.getValue());
        }
    }
}
