import eu.koboo.netsync.NetSyncClient;
import eu.koboo.netsync.NetSyncServer;
import eu.koboo.netsync.operation.SyncModel;
import eu.koboo.netsync.operation.SyncType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncTest {

    static NetSyncServer server;
    static NetSyncClient client;

    @BeforeClass
    public static void before() {
        server = new NetSyncServer();
        Assert.assertTrue(server.start());
        System.out.println("Server started!");
        client = new NetSyncClient("localhost", 2525);
        Assert.assertTrue(client.start());
        System.out.println("Client started!");
    }

    @Test
    public void test() throws InterruptedException {
        SyncModel syncModel = new SyncModel();
        syncModel.put("Geht das wirklich", "ja es geht!");
        syncModel.put("test2", System.currentTimeMillis());
        syncModel.put("isTrue", true);
        client.syncModel(syncModel, SyncType.UPDATE);
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(client != null)
            client.stop();
    }
}
