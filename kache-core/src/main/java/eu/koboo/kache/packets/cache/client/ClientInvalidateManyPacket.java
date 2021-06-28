package eu.koboo.kache.packets.cache.client;

import eu.koboo.endpoint.core.util.BufUtils;
import eu.koboo.kache.packets.cache.CachePacket;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ClientInvalidateManyPacket extends CachePacket {

    private List<String> listToInvalidate;

    public ClientInvalidateManyPacket() {
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
