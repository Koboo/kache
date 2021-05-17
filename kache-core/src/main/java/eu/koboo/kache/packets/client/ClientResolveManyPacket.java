package eu.koboo.kache.packets.client;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ClientResolveManyPacket implements NativePacket {

    private String futureId;
    private String cacheName;
    private List<String> listToResolve;

    public ClientResolveManyPacket() {
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

    public List<String> getListToResolve() {
        return listToResolve;
    }

    public void setListToResolve(List<String> listToResolve) {
        this.listToResolve = listToResolve;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        futureId = BufUtils.readString(byteBuf);
        cacheName = BufUtils.readString(byteBuf);
        int size = BufUtils.readVarInt(byteBuf);
        listToResolve = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            listToResolve.add(key);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(futureId, byteBuf);
        BufUtils.writeString(cacheName, byteBuf);
        int size = listToResolve.size();
        BufUtils.writeVarInt(size, byteBuf);
        for(String key : listToResolve) {
            BufUtils.writeString(key, byteBuf);
        }
    }
}
