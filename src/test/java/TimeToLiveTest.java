import eu.koboo.kache.KacheClient;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimeToLiveTest {

    static KacheClient client;

    static SharedCache<TransferObject> localCache;

    @BeforeClass
    public static void before() throws InterruptedException {

        client = new KacheClient("localhost", 6565);
        client.start();

        client.getTransferCodec().register(1, TransferObject::new);

        localCache = client.getCache("test_cache");
        localCache.timeToLive(TimeUnit.SECONDS.toMillis(10));
    }

    @Test
    public void testA() throws InterruptedException {
        TransferObject transferObject = new TransferObject();
        transferObject.setTestString("Bla");
        transferObject.setTestInt(1);
        transferObject.setTestLong(-1L);

        System.out.println("Exists(before): " + localCache.exists(transferObject.getTestString()).sync());

        localCache.push(transferObject.getTestString(), transferObject);
        System.out.println("Cached!");

        System.out.println("Exists(after): " + localCache.exists(transferObject.getTestString()).sync());

        Thread.sleep(5_000);

        System.out.println("Exists(sleep): " + localCache.exists(transferObject.getTestString()).sync());
    }

    @AfterClass
    public static void after() {
        if(client != null)
            client.stop();
    }
}
