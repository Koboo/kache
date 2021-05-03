package eu.koboo.netsync.event;

import eu.koboo.endpoint.core.Endpoint;
import eu.koboo.endpoint.core.protocols.natives.NativeReceiveEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.event.listener.EventPriority;
import eu.koboo.netsync.operation.SyncModel;
import eu.koboo.netsync.operation.SyncPacket;
import eu.koboo.netsync.operation.SyncType;

public class SyncPacketListener extends EventListener<NativeReceiveEvent> {

    private final Endpoint endpoint;

    public SyncPacketListener(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void onEvent(NativeReceiveEvent event) {
        if(event.getTypeObject() instanceof SyncPacket) {
            SyncPacket packet = (SyncPacket) event.getTypeObject();
            SyncModel syncModel = packet.getSyncModel();
            SyncType syncType = packet.getSyncType();
            endpoint.eventHandler().callEvent(new SyncEvent(syncModel, syncType));
        }
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.HIGHEST;
    }
}
