import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;

public class ResultSyncTest {

    static KacheServer server;
    static KacheClient client;

    static SharedCache<NetworkObj> localCache;

    static Consumer<LogEvent> consumer = e -> System.out.println(e.getMessage());

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.registerEvent(LogEvent.class, consumer);
        server.start();

        client = new KacheClient("localhost", 6565);
        client.registerEvent(LogEvent.class, consumer);
        client.start();

        client.getEncoder().register(1, NetworkObj::new);

        localCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        NetworkObj networkObj = new NetworkObj();
        networkObj.setTestString("Bla");
        networkObj.setTestInt(1);
        networkObj.setTestLong(-1L);

        System.out.println("Exists(before): " + localCache.exists(networkObj.getTestString()).sync());

        Thread.sleep(500);

        localCache.push(networkObj.getTestString(), networkObj);
        System.out.println("Pushed!");

        Thread.sleep(500);

        System.out.println("Exists(after): " + localCache.exists(networkObj.getTestString()).sync());

        Thread.sleep(500);

        networkObj = localCache.resolve(networkObj.getTestString()).sync();
        System.out.println("Resolved!");

        System.out.println("Result: " + (networkObj != null ? networkObj.toString() : "NULL"));

        Thread.sleep(500);

        assertNotNull(networkObj);
        assertNotNull(networkObj.getTestString());

        localCache.invalidate(networkObj.getTestString());
        System.out.println("Invalidated!");

        Thread.sleep(500);

        System.out.println("Exists(invalidated): " + localCache.exists(networkObj.getTestString()).sync());
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(client != null)
            client.stop();
    }
}
