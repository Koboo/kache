package eu.koboo.kache.packets.transfer.client;

import eu.koboo.endpoint.core.util.BufUtils;
import eu.koboo.kache.packets.transfer.TransferPacket;
import io.netty.buffer.ByteBuf;

public class ClientRegisterTransferPacket extends TransferPacket {

    public ClientRegisterTransferPacket() {
    }

    @Override
    public void read(ByteBuf byteBuf) {
        channel = BufUtils.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(channel, byteBuf);
    }
}
