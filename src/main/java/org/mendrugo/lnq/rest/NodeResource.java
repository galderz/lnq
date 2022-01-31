package org.mendrugo.lnq.rest;

import org.bouncycastle.util.encoders.Hex;
import org.mendrugo.lnq.ldk.Node;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/node")
public class NodeResource
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
}