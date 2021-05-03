package eu.koboo.netsync;

import eu.koboo.config.FileConfig;
import eu.koboo.endpoint.server.EndpointServer;
import eu.koboo.netsync.event.SyncPacketListener;

public class NetSyncServer extends EndpointServer {

    public NetSyncServer() {
        super(
                NetSync.ENDPOINT_BUILDER,
                FileConfig.newConfig("port.cfg", cfg -> cfg.init("port", 2525)).getIntOr("port", 2525)
        );
        eventHandler().register(new SyncPacketListener(this));
        eventHandler().register(new ServerSyncListener(this));
    }

}
