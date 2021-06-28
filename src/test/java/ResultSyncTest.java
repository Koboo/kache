import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.cache.SharedCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResultSyncTest {

    static KacheServer server;
    static KacheClient client;

    static SharedCache<NetworkObj> localCache;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();

        client = new KacheClient("localhost", 6565);
        client.start();

        localCache = client.getCache("test_cache");
    }

    @Test
    public void testA() throws InterruptedException {
        NetworkObj networkObj = new NetworkObj("TEst", 1, -1L);

        System.out.println("Exists(before): " + localCache.exists(networkObj.getTestString()).sync());

        localCache.push(networkObj.getTestString(), networkObj);
        System.out.println("Cached!");

        System.out.println("Exists(after): " + localCache.exists(networkObj.getTestString()).sync());

        networkObj = localCache.resolve(networkObj.getTestString()).sync();
        System.out.println("Resolved!");

        System.out.println("Obj: " + (networkObj != null ? networkObj.toString() : "NULL"));

        localCache.invalidate(networkObj.getTestString());
        System.out.println("Invalidated!");

        System.out.println("Exists(invalid): " + localCache.exists(networkObj.getTestString()).sync());
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(client != null)
            client.stop();
    }
}
