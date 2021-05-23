import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeToLiveTest {

    static KacheClient client;

    static SharedCache<TestObj> localCache;

    @BeforeClass
    public static void before() throws InterruptedException {

        client = new KacheClient("localhost", 6565);
        client.start();

        localCache = client.getCache("test_cache");
        localCache.timeToLive(TimeUnit.SECONDS.toMillis(10));
    }

    @Test
    public void testA() throws InterruptedException {
        TestObj testObj = new TestObj("TEst", 1, -1L);

        System.out.println("Exists(before): " + localCache.exists(testObj.getString()).sync());

        localCache.push(testObj.getString(), testObj);
        System.out.println("Cached!");

        System.out.println("Exists(after): " + localCache.exists(testObj.getString()).sync());

        Thread.sleep(5_000);

        System.out.println("Exists(sleep): " + localCache.exists(testObj.getString()).sync());
    }

    @AfterClass
    public static void after() {
        if(client != null)
            client.stop();
    }
}
