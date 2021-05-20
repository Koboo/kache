package eu.koboo.kache.packets.transfer;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;

public abstract class TransferPacket implements NativePacket {

    protected String channel;

    public TransferPacket() {
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
