package eu.koboo.kache.packets.client;

import eu.koboo.kache.packets.CachePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class ClientForceManyPacket extends CachePacket {

    private Map<String, Boolean> forceMap;

    public ClientForceManyPacket() {
    }

    public Map<String, Boolean> getForceMap() {
        return forceMap;
    }

    public void setForceMap(Map<String, Boolean> forceMap) {
        this.forceMap = forceMap;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        cacheName = BufUtils.readString(byteBuf);
        int size = BufUtils.readVarInt(byteBuf);
        forceMap = new HashMap<>();
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            boolean contains = byteBuf.readBoolean();
            forceMap.put(key, contains);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(cacheName, byteBuf);
        int size = forceMap.size();
        BufUtils.writeVarInt(size, byteBuf);
        for(Map.Entry<String, Boolean> entry : forceMap.entrySet()) {
            BufUtils.writeString(entry.getKey(), byteBuf);
            byteBuf.writeBoolean(entry.getValue());
        }
    }
}
