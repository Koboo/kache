import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResultFutureTest {

    static KacheServer server;
    static KacheClient client;

    static SharedCache<TransferObject> sharedCache;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();

        client = new KacheClient("localhost", 6565);
        client.start();

        client.getTransferCodec().register(1, TransferObject::new);

        sharedCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        TransferObject transferObject = new TransferObject();
        transferObject.setTestString("SomeString");
        transferObject.setTestInt(1);
        transferObject.setTestLong(-1L);

        sharedCache.exists(transferObject.getTestString()).future().whenComplete((ex, e) -> System.out.println("Exists(before): " + ex));

        Thread.sleep(500);

        sharedCache.push(transferObject.getTestString(), transferObject);
        System.out.println("Cached!");

        Thread.sleep(500);

        sharedCache.exists(transferObject.getTestString()).future().whenComplete((ex, e) -> System.out.println("Exists(after): " + ex));

        Thread.sleep(500);

        sharedCache.resolve(transferObject.getTestString()).future().whenComplete((obj, e) -> System.out.println("Resolved? " + (obj != null)));

        Thread.sleep(500);

        sharedCache.invalidate(transferObject.getTestString());
        System.out.println("Invalidated!");

        sharedCache.exists(transferObject.getTestString()).future().whenComplete((ex, e) -> System.out.println("Exists(invalid): " + ex));

        Thread.sleep(500);
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(client != null)
            client.stop();
    }
}
