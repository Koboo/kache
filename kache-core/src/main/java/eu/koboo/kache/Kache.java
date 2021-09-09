package eu.koboo.kache;

import eu.koboo.endpoint.core.builder.EndpointBuilder;
import eu.koboo.endpoint.core.builder.param.ErrorMode;
import eu.koboo.endpoint.core.util.Compression;
import eu.koboo.kache.packets.cache.client.*;
import eu.koboo.kache.packets.cache.server.ServerExistsManyPacket;
import eu.koboo.kache.packets.cache.server.ServerResolveManyPacket;
import eu.koboo.kache.packets.transfer.client.ClientRegisterTransferPacket;
import eu.koboo.kache.packets.transfer.client.ClientTransferObjectPacket;
import eu.koboo.kache.packets.transfer.server.ServerTransferObjectPacket;

public class Kache {

    public static final EndpointBuilder ENDPOINT_BUILDER = EndpointBuilder.builder()
            .logging(false)
            .compression(Compression.SNAPPY)
            .errorMode(ErrorMode.STACK_TRACE)
            .useUDS(true)
            .registerPacket(1, ClientTimeToLivePacket.class)
            .registerPacket(2, ClientExistsManyPacket.class)
            .registerPacket(3, ClientForceManyPacket.class)
            .registerPacket(4, ClientInvalidateAllPacket.class)
            .registerPacket(5, ClientInvalidateManyPacket.class)
            .registerPacket(6, ClientPushManyPacket.class)
            .registerPacket(8, ClientResolveAllPacket.class)
            .registerPacket(9, ClientResolveManyPacket.class)
            .registerPacket(10, ClientRegisterTransferPacket.class)
            .registerPacket(11, ClientTransferObjectPacket.class)
            .registerPacket(20, ServerExistsManyPacket.class)
            .registerPacket(21, ServerResolveManyPacket.class)
            .registerPacket(22, ServerTransferObjectPacket.class)
            ;

}
