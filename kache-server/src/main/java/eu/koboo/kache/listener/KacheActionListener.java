package eu.koboo.kache.listener;

import eu.koboo.endpoint.core.events.channel.ChannelActionEvent;
import eu.koboo.event.listener.EventListener;
import eu.koboo.kache.KacheServerApp;
import io.netty.channel.Channel;

public class KacheActionListener extends EventListener<ChannelActionEvent> {

    final KacheServerApp serverApp;

    public KacheActionListener(KacheServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void onEvent(ChannelActionEvent event) {
        Channel channel = event.getChannel();
        if(event.getAction() == ChannelActionEvent.Action.DISCONNECT && serverApp.getServer().hasAnyTransfer(channel)) {
            serverApp.getServer().clearTransfer(channel);
            serverApp.getConsole().info("Transfer{channel=" + event.getChannel().id().toString() + ", action=Reset}");
        }
        serverApp.getConsole().info("Action{channel=" + event.getChannel().id().toString() + ", action=" + event.getAction().name() + "}");
    }
}
