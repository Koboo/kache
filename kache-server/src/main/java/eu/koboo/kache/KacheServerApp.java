package eu.koboo.kache;

import eu.koboo.endpoint.core.events.channel.ChannelActionEvent;
import eu.koboo.endpoint.core.events.message.ErrorEvent;
import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.kache.events.KacheRequestEvent;
import eu.koboo.kache.listener.KacheActionListener;
import eu.koboo.kache.listener.KacheErrorListener;
import eu.koboo.kache.listener.KacheLogListener;
import eu.koboo.kache.listener.KacheRequestListener;
import eu.koboo.terminal.ConsoleBuilder;
import eu.koboo.terminal.TerminalConsole;

public class KacheServerApp {

    final TerminalConsole console;
    final KacheServer kacheServer;

    public KacheServerApp() {
        this.console = ConsoleBuilder.builder()
                .setConsolePrompt(" &6> ")
                .setConsoleName("kache-server")
                .build();
        this.kacheServer = new KacheServer();
        this.kacheServer.registerEvent(ChannelActionEvent.class, new KacheActionListener(this));
        this.kacheServer.registerEvent(KacheRequestEvent.class, new KacheRequestListener(this));
        this.kacheServer.registerEvent(ErrorEvent.class, new KacheErrorListener(this));
        this.kacheServer.registerEvent(LogEvent.class, new KacheLogListener(this));
    }

    void start() {
        kacheServer.start();
        console.info("Started KacheServer!");
        console.start();
    }

    void stop() {
        if (kacheServer != null)
            kacheServer.stop();
    }

    public KacheServer getServer() {
        return kacheServer;
    }

    public TerminalConsole getConsole() {
        return console;
    }

    public static void main(String[] args) {
        KacheServerApp serverApp = new KacheServerApp();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(serverApp::stop));
            serverApp.start();
        } catch (Exception e) {
            serverApp.stop();
            e.printStackTrace();
        }
    }
}
