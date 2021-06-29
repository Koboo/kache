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

    static SharedCache<TransferObject> localCache;

    static Consumer<LogEvent> consumer = e -> System.out.println(e.getMessage());

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.registerEvent(LogEvent.class, consumer);
        server.start();

        client = new KacheClient("localhost", 6565);
        client.registerEvent(LogEvent.class, consumer);
        client.start();

        client.getTransferCodec().register(1, TransferObject::new);

        localCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        TransferObject transferObject = new TransferObject();
        transferObject.setTestString("Bla");
        transferObject.setTestInt(1);
        transferObject.setTestLong(-1L);

        System.out.println("Exists(before): " + localCache.exists(transferObject.getTestString()).sync());

        Thread.sleep(500);

        localCache.push(transferObject.getTestString(), transferObject);
        System.out.println("Pushed!");

        Thread.sleep(500);

        System.out.println("Exists(after): " + localCache.exists(transferObject.getTestString()).sync());

        Thread.sleep(500);

        transferObject = localCache.resolve(transferObject.getTestString()).sync();
        System.out.println("Resolved!");

        System.out.println("Result: " + (transferObject != null ? transferObject.toString() : "NULL"));

        Thread.sleep(500);

        assertNotNull(transferObject);
        assertNotNull(transferObject.getTestString());

        localCache.invalidate(transferObject.getTestString());
        System.out.println("Invalidated!");

        Thread.sleep(500);

        System.out.println("Exists(invalidated): " + localCache.exists(transferObject.getTestString()).sync());
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(client != null)
            client.stop();
    }
}
