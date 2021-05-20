import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResultFutureTest {

    static KacheServer server;
    static KacheClient client;

    static SharedCache<TestObj> sharedCache;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();

        client = new KacheClient("localhost", 6565);
        client.start();

        sharedCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        TestObj testObj = new TestObj("TEst", 1, -1L);

        sharedCache.exists(testObj.getString()).future().whenComplete((ex, e) -> System.out.println("Exists(before): " + ex));

        Thread.sleep(800);

        sharedCache.push(testObj.getString(), testObj);
        System.out.println("Cached!");

        sharedCache.exists(testObj.getString()).future().whenComplete((ex, e) -> System.out.println("Exists(after): " + ex));

        Thread.sleep(800);

        sharedCache.resolve(testObj.getString()).future().whenComplete((obj, e) -> System.out.println("Resolved"));

        Thread.sleep(800);

        sharedCache.invalidate(testObj.getString());
        System.out.println("Invalidated!");

        sharedCache.exists(testObj.getString()).future().whenComplete((ex, e) -> System.out.println("Exists(invalid): " + ex));

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
