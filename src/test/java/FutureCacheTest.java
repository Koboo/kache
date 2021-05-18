import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.CacheType;
import eu.koboo.kache.cache.future.FutureCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FutureCacheTest {

    static KacheServer server;
    static KacheClient client;

    static FutureCache<TestObj> futureCache;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();

        client = new KacheClient("localhost", 6565);
        client.start();

        futureCache = client.getCache("test_cache", CacheType.FUTURE);
    }

    @Test
    public void testA() throws InterruptedException {
        TestObj testObj = new TestObj("TEst", 1, -1L);

        futureCache.exists(testObj.getString()).whenComplete((ex, e) -> System.out.println("Exists(before): " + ex));

        Thread.sleep(800);

        futureCache.push(testObj.getString(), testObj);
        System.out.println("Cached!");

        futureCache.exists(testObj.getString()).whenComplete((ex, e) -> System.out.println("Exists(after): " + ex));

        Thread.sleep(800);

        futureCache.resolve(testObj.getString()).whenComplete((obj, e) -> System.out.println("Resolved"));

        Thread.sleep(800);

        futureCache.invalidate(testObj.getString());
        System.out.println("Invalidated!");

        futureCache.exists(testObj.getString()).whenComplete((ex, e) -> System.out.println("Exists(invalid): " + ex));

        Thread.sleep(800);
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(client != null)
            client.stop();
    }
}
