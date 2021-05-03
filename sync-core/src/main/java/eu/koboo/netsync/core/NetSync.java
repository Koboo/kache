package eu.koboo.netsync.core;

import eu.koboo.endpoint.core.builder.EndpointBuilder;
import eu.koboo.endpoint.core.builder.param.EventMode;
import eu.koboo.endpoint.core.builder.param.Protocol;
import eu.koboo.nettyutils.Compression;

public class NetSync {

    public static EndpointBuilder ENDPOINT_BUILDER = EndpointBuilder.newBuilder()
            .compression(Compression.SNAPPY)
            .protocol(Protocol.NATIVE)
            .eventMode(EventMode.SERVICE);

}
