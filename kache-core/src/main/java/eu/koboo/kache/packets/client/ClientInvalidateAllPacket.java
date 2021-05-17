package eu.koboo.kache.packets.client;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

public class ClientInvalidateAllPacket implements NativePacket {

    private String cacheName;

    public ClientInvalidateAllPacket() {
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        cacheName = BufUtils.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(cacheName, byteBuf);
    }
}
