package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.events.message.ErrorEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.kache.KacheServerApp;

public class KacheErrorListener extends EventListener<ErrorEvent>{

    final KacheServerApp serverApp;

    public KacheErrorListener(KacheServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void onEvent(ErrorEvent errorEvent) {
        serverApp.getConsole().error("Error thrown in class '" + errorEvent.getClazz().getSimpleName() + "'.", errorEvent.getThrowable());
    }
}
