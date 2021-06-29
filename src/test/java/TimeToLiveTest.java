import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeToLiveTest {

    static KacheClient client;

    static SharedCache<NetworkObj> localCache;

    @BeforeClass
    public static void before() throws InterruptedException {

        client = new KacheClient("localhost", 6565);
        client.start();

        client.getEncoder().register(1, NetworkObj::new);

        localCache = client.getCache("test_cache");
        localCache.timeToLive(TimeUnit.SECONDS.toMillis(10));
    }

    @Test
    public void testA() throws InterruptedException {
        NetworkObj networkObj = new NetworkObj();
        networkObj.setTestString("Bla");
        networkObj.setTestInt(1);
        networkObj.setTestLong(-1L);

        System.out.println("Exists(before): " + localCache.exists(networkObj.getTestString()).sync());

        localCache.push(networkObj.getTestString(), networkObj);
        System.out.println("Cached!");

        System.out.println("Exists(after): " + localCache.exists(networkObj.getTestString()).sync());

        Thread.sleep(5_000);

        System.out.println("Exists(sleep): " + localCache.exists(networkObj.getTestString()).sync());
    }

    @AfterClass
    public static void after() {
        if(client != null)
            client.stop();
    }
}
