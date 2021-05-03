package eu.koboo.netsync.operation;

import eu.koboo.endpoint.core.protocols.natives.NativePacket;
import eu.koboo.netsync.NetSync;
import eu.koboo.nettyutils.BufUtils;
import io.netty.buffer.ByteBuf;

import java.util.Map;

public class SyncPacket implements NativePacket {

    private SyncModel syncModel;
    private SyncType syncType;

    public SyncPacket() {
    }

    public SyncModel getSyncModel() {
        return syncModel;
    }

    public SyncType getSyncType() {
        return syncType;
    }

    public void setSyncModel(SyncModel syncModel) {
        this.syncModel = syncModel;
    }

    public void setSyncType(SyncType syncType) {
        this.syncType = syncType;
    }

    @Override
    public void read(ByteBuf byteBuf) {
        syncType = SyncType.valueOf(BufUtils.readString(byteBuf));
        if(syncModel == null)
            syncModel = new SyncModel();
        syncModel.setLastTimeModified(BufUtils.readVarLong(byteBuf));
        long size = BufUtils.readVarLong(byteBuf);
        for(int i = 0; i < size; i++) {
            String key = BufUtils.readString(byteBuf);
            byte[] byteObject = BufUtils.readArray(byteBuf);
            Object object = NetSync.ENDPOINT_BUILDER.getSerializerPool().deserialize(byteObject);
            syncModel.put(key, object);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        BufUtils.writeString(syncType.name(), byteBuf);
        if(syncModel == null)
            syncModel = new SyncModel();
        BufUtils.writeVarLong(syncModel.getLastTimeModified(), byteBuf);
        long size = syncModel.size();
        BufUtils.writeVarLong(size, byteBuf);
        for(Map.Entry<String, Object> entry : syncModel.entrySet()) {
            BufUtils.writeString(entry.getKey(), byteBuf);
            byte[] byteObject = NetSync.ENDPOINT_BUILDER.getSerializerPool().serialize(entry.getValue());
            BufUtils.writeArray(byteObject, byteBuf);
        }
    }

    @Override
    public String toString() {
        return "SyncPacket{" +
                "syncModel=" + syncModel +
                ", syncType=" + syncType.name() +
                '}';
    }
}
