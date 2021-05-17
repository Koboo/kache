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
    public void onEvent(KacheRequestEvent kacheRequestEvent) {
        serverApp.getConsole().info("Packet{channel=" + kacheRequestEvent.getChannelId() + ", type=" + kacheRequestEvent.getPacketClass().getSimpleName().replaceFirst("Client", "").replaceFirst("Packet", "") + ", processTime=" + ((double) kacheRequestEvent.getProcessTime() / 1000) + "ms}");
    }
}
