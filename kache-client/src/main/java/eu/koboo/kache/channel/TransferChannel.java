package eu.koboo.kache.channel;

import eu.koboo.kache.KacheClient;
import eu.koboo.kache.packets.transfer.client.ClientRegisterTransferPacket;
import eu.koboo.kache.packets.transfer.client.ClientTransferObjectPacket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TransferChannel<V extends Serializable> {

    private final KacheClient client;
    private final String channel;
    private final List<Consumer<V>> consumerList;
    private boolean fireReceive;

    public TransferChannel(KacheClient client, String channel) {
        this.client = client;
        this.channel = channel;
        this.consumerList = new ArrayList<>();
        this.fireReceive = true;
        ClientRegisterTransferPacket packet = new ClientRegisterTransferPacket();
        packet.setChannel(channel);
        client.send(packet);
    }

    public String getChannelName() {
        return channel;
    }

    public TransferChannel<V> publish(V value) {
        byte[] valueBytes = client.getSerializerPool().serialize(value);
        ClientTransferObjectPacket packet = new ClientTransferObjectPacket();
        packet.setChannel(channel);
        packet.setValue(valueBytes);
        client.send(packet);
        return this;
    }

    public TransferChannel<V> receive(Consumer<V> valueConsumer) {
        consumerList.add(valueConsumer);
        return this;
    }

    public TransferChannel<V> clearReceivers() {
        consumerList.clear();
        return this;
    }

    public TransferChannel<V> pause(boolean pause) {
        this.fireReceive = pause;
        return this;
    }

    public void onReceive(byte[] object) {
        if(consumerList != null && !consumerList.isEmpty() && fireReceive) {
            V value = client.getSerializerPool().deserialize(object);
            for(Consumer<V> consumer : consumerList) {
                consumer.accept(value);
            }
        }
    }
}
