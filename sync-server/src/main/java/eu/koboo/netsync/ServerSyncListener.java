package eu.koboo.netsync;

import eu.koboo.event.listener.EventListener;
import eu.koboo.netsync.event.SyncEvent;
import eu.koboo.netsync.operation.SyncPacket;

public class ServerSyncListener extends EventListener<SyncEvent> {

    private final NetSyncServer server;

    public ServerSyncListener(NetSyncServer server) {
        this.server = server;
    }

    @Override
    public void onEvent(SyncEvent syncEvent) {
        SyncPacket packet = new SyncPacket();
        packet.setSyncModel(syncEvent.getSyncModel());
        packet.setSyncType(syncEvent.getSyncType());
        System.out.println(packet.toString());
        server.sendAll(packet);
    }
}
