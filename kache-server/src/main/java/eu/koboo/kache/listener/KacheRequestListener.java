package eu.koboo.kache.listener;

import eu.koboo.event.listener.EventListener;
import eu.koboo.kache.KacheServerApp;
import eu.koboo.kache.events.KacheRequestEvent;

public class KacheRequestListener extends EventListener<KacheRequestEvent> {

    final KacheServerApp serverApp;

    public KacheRequestListener(KacheServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void onEvent(KacheRequestEvent event) {
        serverApp.getConsole().info("Packet{channel=" + event.getChannelId() + ", " +
                "type=" + event.getPacketClass().getSimpleName().replaceFirst("Client", "").replaceFirst("Packet", "") + ", " +
                "subject=" + event.getCacheName() + ", " +
                "processTime=" + ((double) event.getProcessTime() / 1000000) + "ms}");
    }
}
