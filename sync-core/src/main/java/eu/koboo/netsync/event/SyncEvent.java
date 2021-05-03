package eu.koboo.netsync.event;

import eu.koboo.event.CallableEvent;
import eu.koboo.netsync.operation.SyncModel;
import eu.koboo.netsync.operation.SyncType;

public class SyncEvent implements CallableEvent {

    private final SyncModel syncModel;
    private final SyncType syncType;

    public SyncEvent(SyncModel syncModel, SyncType syncType) {
        this.syncModel = syncModel;
        this.syncType = syncType;
    }

    public SyncModel getSyncModel() {
        return syncModel;
    }

    public SyncType getSyncType() {
        return syncType;
    }
}
