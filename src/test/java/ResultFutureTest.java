import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResultFutureTest {

    static KacheServer server;
    static KacheClient client;

    static SharedCache<NetworkObj> sharedCache;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();

        client = new KacheClient("localhost", 6565);
        client.start();

        client.getEncoder().register(1, NetworkObj::new);

        sharedCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        NetworkObj networkObj = new NetworkObj();
        networkObj.setTestString("Bla");
        networkObj.setTestInt(1);
        networkObj.setTestLong(-1L);

        sharedCache.exists(networkObj.getTestString()).future().whenComplete((ex, e) -> System.out.println("Exists(before): " + ex));

        Thread.sleep(800);

        sharedCache.push(networkObj.getTestString(), networkObj);
        System.out.println("Cached!");

        sharedCache.exists(networkObj.getTestString()).future().whenComplete((ex, e) -> System.out.println("Exists(after): " + ex));

        Thread.sleep(800);

        sharedCache.resolve(networkObj.getTestString()).future().whenComplete((obj, e) -> System.out.println("Resolved"));

        Thread.sleep(800);

        sharedCache.invalidate(networkObj.getTestString());
        System.out.println("Invalidated!");

        sharedCache.exists(networkObj.getTestString()).future().whenComplete((ex, e) -> System.out.println("Exists(invalid): " + ex));

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
