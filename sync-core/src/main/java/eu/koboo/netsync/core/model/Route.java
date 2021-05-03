package eu.koboo.netsync.core.model;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class Route implements NativePacket {

    private String hostname;
    private List<String> backendList;

    public Route() {
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public List<String> getBackendList() {
        return backendList;
    }

    public void addBackend(String host, int port) {
        if(this.backendList == null)
            this.backendList = new ArrayList<>();
        this.backendList.add(host + ":" + port);
    }

    public void removeBackend(String host, int port) {
        if(this.backendList == null)
            this.backendList = new ArrayList<>();
        this.backendList.remove(host + ":" + port);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.hostname = BufUtils.readString(byteBuf);
        long size = BufUtils.readVarLong(byteBuf);
        for(int i = 0; i < size; i++) {
            String backend = BufUtils.readString(byteBuf);
            String host = backend.split(":")[0];
            int port = Integer.parseInt(backend.split(":")[1]);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(hostname, byteBuf);
        long size = backendList.size();
        BufUtils.writeVarLong(size, byteBuf);
        for(int i = 0; i < size; i++) {
            BufUtils.writeString(backendList.get(i), byteBuf);
        }
    }
}
