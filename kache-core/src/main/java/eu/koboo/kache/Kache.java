package eu.koboo.kache;

import eu.binflux.serial.core.SerializerPool;
import eu.binflux.serial.fst.FSTSerialization;
import eu.koboo.endpoint.core.builder.EndpointBuilder;
import eu.koboo.endpoint.core.builder.param.ErrorMode;
import eu.koboo.endpoint.core.builder.param.EventMode;
import eu.koboo.endpoint.core.builder.param.Protocol;
import eu.koboo.nettyutils.Compression;
import eu.koboo.nettyutils.NettyType;

public class Kache {

    public static final EndpointBuilder ENDPOINT_BUILDER = EndpointBuilder.newBuilder()
            .logging(false)
            .compression(Compression.SNAPPY)
            .protocol(Protocol.NATIVE)
            .eventMode(EventMode.SYNC)
            .errorMode(ErrorMode.STACK_TRACE)
            .serializer(new SerializerPool(FSTSerialization.class))
            .setDomainSocket(NettyType.prepareType().isEpoll() ? "/tmp/kache.sock" : null);

}
