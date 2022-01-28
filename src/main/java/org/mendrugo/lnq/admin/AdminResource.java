package org.mendrugo.lnq.admin;

import org.bouncycastle.util.encoders.Hex;
import org.mendrugo.lnq.ldk.Node;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.net.InetSocketAddress;

@Path("/node")
public class AdminResource
{
    @Inject
    Node node;

    @GET
    @Path("/ping/{message}")
    public String ping(@PathParam("message") String message)
    {
        System.out.println("ENTER ping");
        final String reply = "Hello " + message;
        System.out.println("REPLY ping");
        return reply;
    }

    @GET
    @Path("/info")
    public String info()
    {
        System.out.println("ENTER info");
        final String nodeId = node.nodeId();
        System.out.println("REPLY info");
        return nodeId;
    }

//    @POST
//    @Path("/peer/connect/{nodeId}/{host}/{port}")
//    public void connect(
//        @PathParam("nodeId") String nodeId
//        , @PathParam("host") String host
//        , @PathParam("port") String port
//    )
//    {
//        // TODO reply with a peer list
//        System.out.println("ENTER peer connect");
//        node.connect(Hex.decode(nodeId), host, Integer.parseInt(port));
//        System.out.println("REPLY peer connect");
//    }
}