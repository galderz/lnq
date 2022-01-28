package org.mendrugo.lnq.ldk;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.CommandLineArguments;
import org.bouncycastle.util.encoders.Hex;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.batteries.NioPeerHandler;
import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.ChannelManager;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Logger;
import org.ldk.structs.Persist;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class Node
{
    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    @Inject
    BroadcasterInterface broadcaster;

    @Inject
    ChainMonitor chainMonitor;

    @Inject
    ChannelManagerConstructor channelManagerConstructor;

    @Inject
    Effects effects;

    @Inject
    EventHandler eventHandler;

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

    void onStart(@Observes StartupEvent ev) throws IOException
    {
        System.out.println("Startup Time: " + effects.time());
        System.out.println("Command line args: " + Arrays.toString(args));

        System.out.println("LNQ node fee estimator: " + feeEstimator);
        System.out.println("LNQ node logger: " + logger);
        System.out.println("LNQ node broadcaster: " + broadcaster);
        System.out.println("LNQ node persist: " + persist);
        System.out.println("LNQ node chain monitor: " + chainMonitor);
        System.out.println("LNQ node keys manager: " + keysManager);
        System.out.println("LNQ node channel manager constructor: " + channelManagerConstructor);
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
        final NioPeerHandler peerHandler = channelManagerConstructor.nio_peer_handler;
        peerHandler.interrupt();
        System.out.println("Stopped");
    }

    public String nodeId()
    {
        return Hex.toHexString(channelManagerConstructor.channel_manager.get_our_node_id());
    }

//    public void connect(byte[] nodeId, String host, int port)
//    {
//        try
//        {
//            channelManagerConstructor.nio_peer_handler.connect(
//                nodeId
//                , new InetSocketAddress(host, port)
//                , 10000
//            );
//        }
//        catch (IOException e)
//        {
//            throw new UncheckedIOException(e);
//        }
//    }

    private boolean isConfirmed(byte[] txid)
    {
        return bitcoinService.getRawTransaction(BitcoinRequests.getRawTransaction(txid)).result() != null;
    }
}
