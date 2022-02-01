package org.mendrugo.lnq.rest;

import org.bouncycastle.util.encoders.Hex;
import org.ldk.batteries.ChannelManagerConstructor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

@Path("/peers")
public class PeerResource
{
    @Inject
    ChannelManagerConstructor channelManagerConstructor;

    @GET
    public Set<Peer> list()
    {
        System.out.println("ENTER peer_list");
        final Set<Peer> peers = peers();
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
        connect(Hex.decode(nodeId), host, Integer.parseInt(port));
        System.out.println("REPLY peer connect");
    }

    public void connect(byte[] nodeId, String host, int port)
    {
        try
        {
            channelManagerConstructor.nio_peer_handler.connect(
                nodeId
                , new InetSocketAddress(host, port)
                , 10000
            );
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    public Set<Peer> peers()
    {
        final Set<Peer> peers = new HashSet<>();
        for (byte[] peerIdBytes : channelManagerConstructor.peer_manager.get_peer_node_ids())
        {
            peers.add(new Peer(Hex.toHexString(peerIdBytes)));
        }
        return peers;
    }
}
