package eu.koboo.kache.packets.transfer;

import eu.koboo.endpoint.core.codec.EndpointPacket;

public abstract class TransferPacket implements EndpointPacket {

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
