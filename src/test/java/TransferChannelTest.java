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

    static TransferChannel<TestObj> transfer;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();
        clientList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            KacheClient receiveClient = new KacheClient("localhost", 6565);
            receiveClient.start();
            TransferChannel<TestObj> channel = receiveClient.getTransfer("test_transfer");
            channel.receive(obj -> System.out.println(obj.getString() + "/" + obj.getInte() + "/" + obj.getLon()));
            clientList.add(receiveClient);
            Thread.sleep(50L);
        }

        client = new KacheClient("localhost", 6565);
        client.start();

        transfer = client.getTransfer("test_transfer");
    }

    @Test
    public void testA() throws InterruptedException {
        TestObj testObj = new TestObj("TEst", 1, -1L);
        transfer.publish(testObj);

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
