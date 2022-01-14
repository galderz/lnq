package org.mendrugo.lnq.ldk;

import io.quarkus.runtime.StartupEvent;
import org.bouncycastle.util.encoders.Hex;
import org.ldk.batteries.NioPeerHandler;
import org.ldk.structs.ChannelManager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;

@ApplicationScoped
public class LnPeerBinder
{
    @Inject
    LnChannelManagerConstructor channelManagerConstructor;

    @SuppressWarnings("unused") // dependency only to make sure chain sync has completed
    @Inject
    LnSyncStartup syncStartup;

    @PostConstruct
    void onStart() throws IOException
    {
        final NioPeerHandler peerHandler = channelManagerConstructor.constructor.nio_peer_handler;
        final ChannelManager channelManager = channelManagerConstructor.constructor.channel_manager;
        final int port = 9730;
        peerHandler.bind_listener(new InetSocketAddress("127.0.0.1", port));
        System.out.printf("p2p %s 127.0.0.1:%d%n"
            , Hex.toHexString(channelManager.get_our_node_id())
            , port
        );
    }
}
