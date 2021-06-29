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

    static TransferChannel<TransferObject> transfer;

    @BeforeClass
    public static void before() throws InterruptedException {
        server = new KacheServer(6565);
        server.start();
        clientList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            KacheClient receiveClient = new KacheClient("localhost", 6565);
            receiveClient.getTransferCodec().register(1, TransferObject::new);
            boolean started = receiveClient.start();
            if(started) {
                TransferChannel<TransferObject> channel = receiveClient.getTransfer("test_transfer");
                channel.receive(obj -> System.out.println(obj.getTestString() + "/" + obj.getTestInt() + "/" + obj.getTestLong()));
                clientList.add(receiveClient);
                Thread.sleep(500);
            }
        }

        client = new KacheClient("localhost", 6565);
        client.getTransferCodec().register(1, TransferObject::new);
        client.start();

        transfer = client.getTransfer("test_transfer");
    }

    @Test
    public void testA() throws InterruptedException {
        TransferObject transferObject = new TransferObject();
        transferObject.setTestString("Bla");
        transferObject.setTestInt(1);
        transferObject.setTestLong(-1L);

        transfer.publish(transferObject);
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
