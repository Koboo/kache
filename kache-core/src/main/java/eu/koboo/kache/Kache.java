package eu.koboo.kache;

import eu.binflux.serial.core.SerializerPool;
import eu.binflux.serial.fst.FSTSerialization;
import eu.koboo.endpoint.core.builder.EndpointBuilder;
import eu.koboo.endpoint.core.builder.param.ErrorMode;
import eu.koboo.endpoint.core.builder.param.EventMode;
import eu.koboo.endpoint.core.builder.param.Protocol;
import eu.koboo.kache.packets.cache.client.*;
import eu.koboo.kache.packets.cache.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.cache.server.ServerResolveManyPacket;
import eu.koboo.kache.packets.transfer.client.ClientRegisterTransferPacket;
import eu.koboo.kache.packets.transfer.client.ClientTransferObjectPacket;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;
import eu.koboo.nettyutils.Compression;
import eu.koboo.nettyutils.NettyType;

public class Kache {

    public static final EndpointBuilder ENDPOINT_BUILDER = EndpointBuilder.newBuilder()
            .logging(false)
            .compression(Compression.NONE)
            .protocol(Protocol.NATIVE)
            .eventMode(EventMode.SYNC)
            .errorMode(ErrorMode.STACK_TRACE)
            .serializer(new SerializerPool(FSTSerialization.class))
            .setDomainSocket(NettyType.prepareType().isEpoll() ? "/tmp/kache.sock" : null)
            .registerNative(1, ClientTimeToLivePacket.class)
            .registerNative(2, ClientExistsManyPacket.class)
            .registerNative(3, ClientForceManyPacket.class)
            .registerNative(4, ClientInvalidateAllPacket.class)
            .registerNative(5, ClientInvalidateManyPacket.class)
            .registerNative(6, ClientPushManyPacket.class)
            .registerNative(8, ClientResolveAllPacket.class)
            .registerNative(9, ClientResolveManyPacket.class)
            .registerNative(10, ClientRegisterTransferPacket.class)
            .registerNative(11, ClientTransferObjectPacket.class)
            .registerNative(20, ServerExistsManyPacket.class)
            .registerNative(21, ServerResolveManyPacket.class)
            .registerNative(22, ServerTransferObjectPacket.class)
            ;

}
