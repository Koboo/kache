package eu.koboo.kache.packets.client;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ClientInvalidateManyPacket implements NativePacket {

    private String cacheName;
    private List<String> listToInvalidate;

    public ClientInvalidateManyPacket() {
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public List<String> getListToInvalidate() {
        return listToInvalidate;
    }

    public void setListToInvalidate(List<String> listToInvalidate) {
        this.listToInvalidate = listToInvalidate;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        cacheName = BufUtils.readString(byteBuf);
        int size = BufUtils.readVarInt(byteBuf);
        listToInvalidate = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            listToInvalidate.add(key);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(cacheName, byteBuf);
        int size = listToInvalidate.size();
        BufUtils.writeVarInt(size, byteBuf);
        for(String key : listToInvalidate) {
            BufUtils.writeString(key, byteBuf);
        }
    }
}
