package org.mendrugo.lnq.node;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/node")
public class NodeResource
{
    @GET
    @Path("/ping/{message}")
    public String ping(@PathParam("message") String message)
    {
        System.out.println("ENTER ping");
        final String reply = "Hello " + message;
        System.out.println("REPLY ping");
        return reply;
    }
}