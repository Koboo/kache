import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.future.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReconnectTest {

    static KacheClient client;

    static SharedCache<TestObj> localCache;

    @BeforeClass
    public static void before() {
        client = new KacheClient("localhost", 6565);
        client.start();

        localCache = client.getCache("test_cache");
    }

    @Test
    public void testA() {
        TestObj testObj = new TestObj("TEst", 1, -1L);

        System.out.println("Exists(before): " + localCache.exists(testObj.getString()).sync());

        localCache.push(testObj.getString(), testObj);
        System.out.println("Cached!");

        System.out.println("Exists(after): " + localCache.exists(testObj.getString()).sync());

        testObj = localCache.resolve(testObj.getString()).sync();
        System.out.println("Resolved!");

        System.out.println("Obj: " + (testObj != null ? testObj.toString() : "NULL"));

        localCache.invalidate(testObj.getString());
        System.out.println("Invalidated!");

        System.out.println("Exists(invalid): " + localCache.exists(testObj.getString()).sync());
    }

    @AfterClass
    public static void after() {
        if(client != null)
            client.stop();
    }
}
