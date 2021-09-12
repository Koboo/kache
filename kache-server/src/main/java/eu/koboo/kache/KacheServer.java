package eu.koboo.kache;

import eu.koboo.endpoint.core.builder.param.ErrorMode;
import eu.koboo.endpoint.core.events.ReceiveEvent;
import eu.koboo.endpoint.server.EndpointServer;
import eu.koboo.kache.listener.KacheServerListener;
import eu.koboo.kache.manager.CacheManager;
import eu.koboo.kache.manager.TransferManager;
import io.netty.channel.epoll.Epoll;

public class KacheServer extends EndpointServer {

    private final CacheManager cacheManager;
    private final TransferManager transferManager;

    public KacheServer() {
        this(6565);
    }

    public KacheServer(int port) {
        super(Kache.ENDPOINT_BUILDER.errorMode(ErrorMode.EVENT), port);
        registerEvent(ReceiveEvent.class, new KacheServerListener(this));
        cacheManager = new CacheManager(this);
        transferManager = new TransferManager(this);
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public TransferManager getTransferManager() {
        return transferManager;
    }
}
