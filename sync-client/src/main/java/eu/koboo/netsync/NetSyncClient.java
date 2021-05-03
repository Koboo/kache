package eu.koboo.netsync;

import eu.koboo.endoint.client.EndpointClient;
import eu.koboo.netsync.event.SyncPacketListener;
import eu.koboo.netsync.operation.SyncModel;
import eu.koboo.netsync.operation.SyncPacket;
import eu.koboo.netsync.operation.SyncType;

public class NetSyncClient extends EndpointClient {

    public NetSyncClient(String host, int port) {
        super(NetSync.ENDPOINT_BUILDER, host, port);
        eventHandler().register(new SyncPacketListener(this));
    }

    public void syncModel(SyncModel syncModel, SyncType syncType) {
        SyncPacket packet = new SyncPacket();
        packet.setSyncModel(syncModel);
        packet.setSyncType(syncType);
        send(packet, true);
    }
}
