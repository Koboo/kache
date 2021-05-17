package eu.koboo.kache.packets.client;

import eu.koboo.kache.packets.CachePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

public class ClientInvalidateAllPacket extends CachePacket {

    public ClientInvalidateAllPacket() {
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
