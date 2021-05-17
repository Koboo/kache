package eu.koboo.kache.packets.client;

import eu.koboo.kache.packets.CachePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ClientExistsManyPacket extends CachePacket {

    private String futureId;
    private List<String> listToContains;

    public ClientExistsManyPacket() {
    }

    public String getFutureId() {
        return futureId;
    }

    public void setFutureId(String futureId) {
        this.futureId = futureId;
    }

    public List<String> getListToContains() {
        return listToContains;
    }

    public void setListToContains(List<String> listToContains) {
        this.listToContains = listToContains;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        futureId = BufUtils.readString(byteBuf);
        cacheName = BufUtils.readString(byteBuf);
        int size = BufUtils.readVarInt(byteBuf);
        listToContains = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            listToContains.add(key);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(futureId, byteBuf);
        BufUtils.writeString(cacheName, byteBuf);
        int size = listToContains.size();
        BufUtils.writeVarInt(size, byteBuf);
        for(String key : listToContains) {
            BufUtils.writeString(key, byteBuf);
        }
    }
}
