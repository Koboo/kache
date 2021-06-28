package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.kache.KacheServerApp;

import java.util.function.Consumer;

public class KacheLogListener implements Consumer<LogEvent> {

    private final KacheServerApp serverApp;

    public KacheLogListener(KacheServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void accept(LogEvent logEvent) {
        serverApp.getConsole().info(logEvent.getMessage());
    }

}
