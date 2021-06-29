package eu.koboo.kache.manager;

import eu.koboo.endpoint.core.events.message.LogEvent;
import eu.koboo.kache.KacheServer;
import eu.koboo.kache.packets.transfer.client.ClientRegisterTransferPacket;
import eu.koboo.kache.packets.transfer.client.ClientTransferObjectPacket;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransferManager {

    private final KacheServer server;
    private final Map<String, List<String>> serverChannelRegistry = new ConcurrentHashMap<>();

    public TransferManager(KacheServer server) {
        this.server = server;
    }

    public void registerTransfer(Channel channel, ClientRegisterTransferPacket packet) {
        String transferChannel = packet.getChannel();
        String id = channel.id().toString();
        List<String> channelList = serverChannelRegistry.getOrDefault(id, new ArrayList<>());
        if (!channelList.contains(transferChannel)) {
            channelList.add(transferChannel);
            if(!serverChannelRegistry.containsKey(id))
                serverChannelRegistry.put(id, channelList);
            server.fireEvent(new LogEvent(id + " > Registered transfer-channel '" + transferChannel + "'"));
        }
    }

    public void transfer(Channel channel, ClientTransferObjectPacket packet) {
        String transferChannel = packet.getChannel();
        byte[] value = packet.getValue();
        ServerTransferObjectPacket response = new ServerTransferObjectPacket();
        response.setChannel(transferChannel);
        response.setValue(value);
        for(Channel clients : server.getChannelGroup()) {
            String id = clients.id().toString();
            if(!id.equals(channel.id().toString()) && hasTransfer(clients, transferChannel)) {
                clients.writeAndFlush(response);
            }
        }
    }

    public boolean hasTransfer(Channel channel, String transferChannel) {
        String id = channel.id().toString();
        return serverChannelRegistry.getOrDefault(id, new ArrayList<>()).contains(transferChannel);
    }

    public boolean hasAnyTransfer(Channel channel) {
        return serverChannelRegistry.containsKey(channel.id().toString());
    }

    public void clearTransfer(Channel channel) {
        serverChannelRegistry.remove(channel.id().toString());
    }
}
