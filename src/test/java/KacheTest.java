import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.LocalCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class KacheTest {

    static KacheServer server;
    static KacheClient client;

    static LocalCache<TestObj> localCache;

    @BeforeClass
    public static void before() {
        server = new KacheServer(6565);
        server.start();
        client = new KacheClient("localhost", 6565);
        client.start();

        localCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        TestObj testObj = new TestObj("TEst", 1, -1L);

        System.out.println("Exists(before): " + localCache.exists(testObj.getString()));

        Thread.sleep(500L);

        localCache.cache(testObj.getString(), testObj);
        System.out.println("Cached!");

        Thread.sleep(500L);

        System.out.println("Exists(after): " + localCache.exists(testObj.getString()));

        Thread.sleep(500L);

        testObj = localCache.resolve(testObj.getString());
        System.out.println("Resolved!");

        System.out.println("Obj: " + (testObj != null ? testObj.toString() : "NULL"));

        Thread.sleep(500L);

        localCache.invalidate(testObj.getString());
        System.out.println("Invalidated!");

        Thread.sleep(500L);

        System.out.println("Exists(invalid): " + localCache.exists(testObj.getString()));
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(client != null)
            client.stop();
    }
}
