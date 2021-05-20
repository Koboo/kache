package eu.koboo.kache.packets.transfer.server;

import eu.koboo.kache.packets.transfer.TransferPacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

public class ServerTransferObjectPacket extends TransferPacket {

    private byte[] value;

    public ServerTransferObjectPacket() {
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        channel = BufUtils.readString(byteBuf);
        value = BufUtils.readArray(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(channel, byteBuf);
        BufUtils.writeArray(value, byteBuf);
    }
}
