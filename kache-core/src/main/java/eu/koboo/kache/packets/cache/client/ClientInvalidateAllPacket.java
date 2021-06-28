package eu.koboo.kache.packets.cache.client;

import eu.koboo.endpoint.core.util.BufUtils;
import eu.koboo.kache.packets.cache.CachePacket;
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
