package org.mendrugo.lnq.ldk;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.CommandLineArguments;
import org.bouncycastle.util.encoders.Hex;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.batteries.NioPeerHandler;
import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.ChannelManager;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Logger;
import org.ldk.structs.Persist;
import org.ldk.structs.Result__u832APIErrorZ;
import org.mendrugo.lnq.bitcoin.BitcoinService;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class Node
{
    @Inject
    BitcoinService bitcoinService;

    @Inject
    BroadcasterInterface broadcaster;

    @Inject
    ChainMonitor chainMonitor;

    @Inject
    ChannelManagerConstructor channelManagerConstructor;

    @Inject
    Effects effects;

    @Inject
    FeeEstimator feeEstimator;

    @Inject
    KeysManager keysManager;

    @Inject
    Logger logger;

    @Inject
    Persist persist;

    @Inject
    @CommandLineArguments
    String[] args;

    @Inject
    org.jboss.logging.Logger jbossLog;

    void onStart(@Observes StartupEvent ev) throws IOException
    {
        jbossLog.infof("Startup Time: %s", effects.time());
        jbossLog.infof("Command line args: %s", Arrays.toString(args));

        jbossLog.infof("LNQ node fee estimator: %s", feeEstimator);
        jbossLog.infof("LNQ node logger: %s", logger);
        jbossLog.infof("LNQ node broadcaster: %s", broadcaster);
        jbossLog.infof("LNQ node persist: %s", persist);
        jbossLog.infof("LNQ node chain monitor: %s", chainMonitor);
        jbossLog.infof("LNQ node keys manager: %s", keysManager);
        jbossLog.infof("LNQ node channel manager constructor: %s", channelManagerConstructor);
        System.out.println("LNQ node channel manager: " + channelManagerConstructor.channel_manager);

        final ChannelManager channelManager = channelManagerConstructor.channel_manager;
        byte[][] channelManagerTxIds = channelManager.as_Confirm().get_relevant_txids();
        byte[][] chainTxIds = chainMonitor.as_Confirm().get_relevant_txids();
        final var allTxIds = new ArrayList<>(List.of(channelManagerTxIds));
        allTxIds.addAll(List.of(chainTxIds));
        allTxIds.stream()
            .filter(txid -> !isConfirmed(txid))
            .forEach(txid ->
            {
                channelManager.as_Confirm().transaction_unconfirmed(txid);
                chainMonitor.as_Confirm().transaction_unconfirmed(txid);
            });

        final EventHandler eventHandler = new EventHandler(bitcoinService, channelManagerConstructor, effects, feeEstimator, keysManager, jbossLog);
        channelManagerConstructor.chain_sync_completed(eventHandler, null);

        final NioPeerHandler peerHandler = channelManagerConstructor.nio_peer_handler;
        final int port = 9730;
        peerHandler.bind_listener(new InetSocketAddress("127.0.0.1", port));
        System.out.printf("LNQ node %s 127.0.0.1:%d%n"
            , Hex.toHexString(channelManager.get_our_node_id())
            , port
        );
    }

    void onStop(@Observes ShutdownEvent ev)
    {
        System.out.println("Stopping...");
        //System.out.println("Before stoppping, peers are: " + PeerResource.peers(channelManagerConstructor.peer_manager));
        channelManagerConstructor.nio_peer_handler.interrupt();
        System.out.println("Stopped");
    }

    public String nodeId()
    {
        return Hex.toHexString(channelManagerConstructor.channel_manager.get_our_node_id());
    }

    private boolean isConfirmed(byte[] txId)
    {
        return bitcoinService.getRawTransaction(txId) != null;
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
        // System.out.println("Get peers from " + peerManager);
        final Set<Peer> peers = new HashSet<>();
        for (byte[] peerIdBytes : channelManagerConstructor.peer_manager.get_peer_node_ids())
        {
            peers.add(new Peer(Hex.toHexString(peerIdBytes)));
        }
        return peers;
    }

    public void channelNew(byte[] nodeId, long value)
    {
        final ChannelManager channelManager = channelManagerConstructor.channel_manager;
        final Result__u832APIErrorZ result = channelManager.create_channel(nodeId, value, 0, 0, null);
        if (!result.is_ok())
            throw new RuntimeException("Unable to create new channel");
    }
}
