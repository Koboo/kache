import eu.koboo.kache.KacheClient;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.channel.TransferChannel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TransferChannelTest {

    static KacheServer server;
    static KacheClient client;
    static List<KacheClient> clientList;

    static TransferChannel<NetworkObj> transfer;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();
        clientList = new ArrayList<>();

        client.getEncoder().register(1, NetworkObj::new);

        for(int i = 0; i < 3; i++) {
            KacheClient receiveClient = new KacheClient("localhost", 6565);
            receiveClient.start();
            TransferChannel<NetworkObj> channel = receiveClient.getTransfer("test_transfer");
            channel.receive(obj -> System.out.println(obj.getTestString() + "/" + obj.getTestInt() + "/" + obj.getTestLong()));
            clientList.add(receiveClient);
            Thread.sleep(50L);
        }

        client = new KacheClient("localhost", 6565);
        client.start();

        transfer = client.getTransfer("test_transfer");
    }

    @Test
    public void testA() throws InterruptedException {
        NetworkObj networkObj = new NetworkObj();
        networkObj.setTestString("Bla");
        networkObj.setTestInt(1);
        networkObj.setTestLong(-1L);

        transfer.publish(networkObj);

        Thread.sleep(5_000L);
    }

    @AfterClass
    public static void after() {
        if(server != null)
            server.stop();
        if(clientList != null && !clientList.isEmpty())
            for(KacheClient receiveClient : clientList) {
                receiveClient.stop();
            }
        if(client != null)
            client.stop();
    }
}
