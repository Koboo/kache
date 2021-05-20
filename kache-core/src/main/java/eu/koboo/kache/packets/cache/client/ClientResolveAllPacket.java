package eu.koboo.kache.packets.cache.client;

import eu.koboo.kache.packets.cache.CachePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

public class ClientResolveAllPacket extends CachePacket {

    private String futureId;

    public ClientResolveAllPacket() {
    }

    public String getFutureId() {
        return futureId;
    }

    public void setFutureId(String futureId) {
        this.futureId = futureId;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        futureId = BufUtils.readString(byteBuf);
        cacheName = BufUtils.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(futureId, byteBuf);
        BufUtils.writeString(cacheName, byteBuf);
    }
}
