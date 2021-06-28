package eu.koboo.kache.packets.transfer.client;

import eu.koboo.endpoint.core.util.BufUtils;
import eu.koboo.kache.packets.transfer.TransferPacket;
import io.netty.buffer.ByteBuf;

public class ClientTransferObjectPacket extends TransferPacket {

    private byte[] value;

    public ClientTransferObjectPacket() {
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
