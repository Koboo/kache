package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.kache.KacheServerApp;

public class KacheLogListener extends EventListener<LogEvent> {

    private final KacheServerApp serverApp;

    public KacheLogListener(KacheServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void onEvent(LogEvent logEvent) {
        serverApp.getConsole().info(logEvent.getMessage());
    }
}
