package org.mendrugo.lnq.node;

import org.mendrugo.lnq.ldk.Node;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

// TODO rename package and name to admin
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
        return node.nodeId();
    }
}