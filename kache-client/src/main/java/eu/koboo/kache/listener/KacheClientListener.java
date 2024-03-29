package eu.koboo.kache.listener;

import static eu.koboo.endpoint.core.util.SwitchClass.ccase;
import static eu.koboo.endpoint.core.util.SwitchClass.cswitch;

import eu.koboo.endpoint.core.events.ReceiveEvent;
import eu.koboo.endpoint.core.util.SharedFutures;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.channel.TransferChannel;
import eu.koboo.kache.packets.cache.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.cache.server.ServerResolveManyPacket;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class KacheClientListener implements Consumer<ReceiveEvent> {

  final KacheClient client;

  public KacheClientListener(KacheClient client) {
    this.client = client;
  }

  @Override
  public void accept(ReceiveEvent event) {
    cswitch(event.getTypeObject(),
        ccase(ServerResolveManyPacket.class, p -> {
          String futureId = p.getFutureId();
          CompletableFuture<Map<String, ?>> future = SharedFutures.getFuture(futureId);
          Map<String, Serializable> serializedMap = new HashMap<>();
          if (p.getMapToResolve() != null && !p.getMapToResolve().isEmpty()) {
            for (Map.Entry<String, byte[]> entry : p.getMapToResolve().entrySet()) {
              Serializable value = client.getSerializerPool().deserialize(entry.getValue());
              serializedMap.put(entry.getKey(), value);
            }
          }
          future.complete(serializedMap);
        }),
        ccase(ServerExistsManyPacket.class, p -> {
          String futureId = p.getFutureId();
          CompletableFuture<Map<String, Boolean>> future = SharedFutures.getFuture(futureId);
          if (future != null) {
            future.complete(p.getMapToContains());
          }
        }),
        ccase(ServerTransferObjectPacket.class, p -> {
          TransferChannel<?> transferChannel = client.getTransfer(p.getChannel());
          transferChannel.onReceive(p.getValue());
        }));
  }
}
