package org.mendrugo.lnq.node;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import org.mendrugo.lnq.node.proto.NodeService;
import org.mendrugo.lnq.node.proto.PingReply;
import org.mendrugo.lnq.node.proto.PingRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/node")
public class NodeResource
{
    @GrpcClient
    NodeService node;

    @GET
    @Path("/ping/{message}")
    public Uni<String> ping(@PathParam("message") String message)
    {
        final PingRequest request = PingRequest.newBuilder().setMessage(message).build();
        return node.ping(request)
            .onItem()
            .transform(PingReply::getMessage);
    }
}