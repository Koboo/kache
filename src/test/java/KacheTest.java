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

        localCache.push(testObj.getString(), testObj);
        System.out.println("Cached!");

        System.out.println("Exists(after): " + localCache.exists(testObj.getString()));

        testObj = localCache.resolve(testObj.getString());
        System.out.println("Resolved!");

        System.out.println("Obj: " + (testObj != null ? testObj.toString() : "NULL"));

        localCache.invalidate(testObj.getString());
        System.out.println("Invalidated!");

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
