package eu.koboo.kache.channel;

import eu.koboo.kache.KacheClient;
import eu.koboo.kache.packets.transfer.client.ClientRegisterTransferPacket;
import eu.koboo.kache.packets.transfer.client.ClientTransferObjectPacket;

import java.io.Serializable;
import java.util.function.Consumer;

public class TransferChannelImpl<V extends Serializable> implements TransferChannel<V> {

    private final KacheClient client;
    private final String channel;
    private Consumer<V> consumer;

    public TransferChannelImpl(KacheClient client, String channel) {
        this.client = client;
        this.channel = channel;
        ClientRegisterTransferPacket packet = new ClientRegisterTransferPacket();
        packet.setChannel(channel);
        client.send(packet, false);
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public TransferChannel<V> publish(V value) {
        byte[] valueBytes = client.builder().getSerializerPool().serialize(value);
        ClientTransferObjectPacket packet = new ClientTransferObjectPacket();
        packet.setChannel(channel);
        packet.setValue(valueBytes);
        client.send(packet, false);
        return this;
    }

    @Override
    public TransferChannel<V> receive(Consumer<V> valueConsumer) {
        this.consumer = valueConsumer;
        return this;
    }

    public void onReceive(byte[] object) {
        if(consumer != null) {
            V value = client.builder().getSerializerPool().deserialize(object);
            consumer.accept(value);
        }

    }
}
