package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.events.message.ErrorEvent;
import eu.koboo.kache.KacheServerApp;

import java.util.function.Consumer;

public class KacheErrorListener implements Consumer<ErrorEvent> {

    final KacheServerApp serverApp;

    public KacheErrorListener(KacheServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void accept(ErrorEvent errorEvent) {
        serverApp.getConsole().error("Error thrown in class '" + errorEvent.getClazz().getSimpleName() + "'.", errorEvent.getThrowable());
    }

}
