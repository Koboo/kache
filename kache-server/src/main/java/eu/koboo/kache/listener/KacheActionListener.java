package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.events.channel.ChannelActionEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.kache.KacheServerApp;

public class KacheActionListener extends EventListener<ChannelActionEvent> {

    final KacheServerApp serverApp;

    public KacheActionListener(KacheServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void onEvent(ChannelActionEvent event) {
        serverApp.getConsole().info("Action{channel=" + event.getChannel().id().toString() + ", action=" + event.getAction().name() + "}");
    }
}
