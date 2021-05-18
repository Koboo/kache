package eu.koboo.kache.packets.client;

import eu.koboo.kache.packets.CachePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

public class ClientTimeToLivePacket extends CachePacket {

    private long cacheTimeMillis;

    public ClientTimeToLivePacket() {
    }

    public long getCacheTimeMillis() {
        return cacheTimeMillis;
    }

    public void setCacheTimeMillis(long cacheTimeMillis) {
        this.cacheTimeMillis = cacheTimeMillis;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        cacheName = BufUtils.readString(byteBuf);
        cacheTimeMillis = BufUtils.readVarLong(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(cacheName, byteBuf);
        BufUtils.writeVarLong(cacheTimeMillis, byteBuf);
    }
}
