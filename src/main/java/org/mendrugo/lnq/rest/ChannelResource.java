package org.mendrugo.lnq.rest;

import org.bouncycastle.util.encoders.Hex;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.mendrugo.lnq.ldk.Node;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/channels")
public class ChannelResource
{
    @Inject
    Node node;

    @Inject
    org.jboss.logging.Logger jbossLog;

    @POST
    @Path("/new/{nodeId}/{value}")
    public void new_(@PathParam String nodeId, @PathParam long value)
    {
        // TODO reply with a channel list
        jbossLog.infof("ENTER channel_new");
        // TODO use java 17 API for hex
        node.channelNew(Hex.decode(nodeId), value);
        jbossLog.info("Created");
        jbossLog.infof("REPLY channel_new");
    }
}