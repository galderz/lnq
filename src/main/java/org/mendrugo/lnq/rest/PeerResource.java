package org.mendrugo.lnq.rest;

import org.bouncycastle.util.encoders.Hex;
import org.mendrugo.lnq.ldk.Node;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/peers")
public class PeerResource
{
    @Inject
    Node node;

    @POST
    @Path("/connect/{nodeId}/{host}/{port}")
    public void connect(
        @PathParam("nodeId") String nodeId
        , @PathParam("host") String host
        , @PathParam("port") String port
    )
    {
        // TODO reply with a peer list
        System.out.println("ENTER peer connect");
        node.connect(Hex.decode(nodeId), host, Integer.parseInt(port));
        System.out.println("REPLY peer connect");
    }
}
