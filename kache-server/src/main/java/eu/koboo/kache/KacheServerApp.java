package eu.koboo.kache;

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
