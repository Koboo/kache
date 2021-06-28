import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReconnectTest {

    static KacheClient client;

    static SharedCache<NetworkObj> localCache;

    @BeforeClass
    public static void before() {
        client = new KacheClient("localhost", 6565);
        client.start();

        localCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        Thread.sleep(1000L * 60);
    }

    @AfterClass
    public static void after() {
        if(client != null)
            client.stop();
    }
}
