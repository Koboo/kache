package eu.koboo.kache.channel;

import java.io.Serializable;
import java.util.function.Consumer;

public interface TransferChannel<V extends Serializable> {

    String getChannel();

    TransferChannel<V> publish(V value);

    TransferChannel<V> receive(Consumer<V> valueConsumer);

    TransferChannel<V> pause(boolean pause);

}
