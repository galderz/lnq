package org.mendrugo.lnq.rest;

import org.bouncycastle.util.encoders.Hex;
import org.mendrugo.lnq.ldk.Node;
import org.mendrugo.lnq.ldk.Peer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Set;

@Path("/peers")
public class PeerResource
{
    @Inject
    Node node;

    @GET
    public Set<Peer> list()
    {
        System.out.println("ENTER peer_list");
        final Set<Peer> peers = node.peers();
        System.out.println("REPLY peer_list");
        return peers;
    }

    // TODO switch to JSON object, see https://quarkus.io/guides/rest-json
    //      Then to curl it use https://reqbin.com/req/c-dwjszac0/curl-post-json-example
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
