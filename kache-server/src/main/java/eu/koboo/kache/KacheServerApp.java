package eu.koboo.kache;

import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.kache.listener.KacheActionListener;
import eu.koboo.kache.listener.KacheErrorListener;
import eu.koboo.kache.listener.KacheLogListener;
import eu.koboo.kache.listener.KacheRequestListener;
import eu.koboo.terminal.TerminalConsole;
import eu.koboo.terminal.ConsoleBuilder;

public class KacheServerApp {

    final TerminalConsole console;
    final KacheServer kacheServer;

    public KacheServerApp() {
        this.console = ConsoleBuilder.builder()
                .setConsolePrompt(" &6> ")
                .setConsoleName("kache-server")
                .build();
        this.kacheServer = new KacheServer();
        this.kacheServer.eventHandler().register(new KacheActionListener(this));
        this.kacheServer.eventHandler().register(new KacheRequestListener(this));
        this.kacheServer.eventHandler().register(new KacheErrorListener(this));
        this.kacheServer.eventHandler().register(new KacheLogListener(this));
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
