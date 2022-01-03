package org.mendrugo.lnq.node;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/node")
public class NodeResource
{
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello()
    {
        return "hola";
    }
}