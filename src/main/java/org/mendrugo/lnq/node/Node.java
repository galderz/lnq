package org.mendrugo.lnq.node;

import io.quarkus.runtime.StartupEvent;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bouncycastle.util.encoders.Hex;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.enums.ConfirmationTarget;
import org.ldk.enums.Network;
import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.ChannelManager;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Logger;
import org.ldk.structs.Option_FilterZ;
import org.ldk.structs.Persist;
import org.ldk.structs.UserConfig;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;
import org.mendrugo.lnq.bitcoin.BlockchainInfo;
import org.mendrugo.lnq.bitcoin.SendRawTransaction;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.bitcoinj.core.NetworkParameters.ID_REGTEST;

@ApplicationScoped
public class Node //implements QuarkusApplication
{
    @Inject
    Effects effects;

    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    void onStart(@Observes StartupEvent ev)
    {
        System.out.println("Do startup logic here");
        System.out.println("Time: " + effects.time());

        setup();
    }

//    @Override
//    public int run(String... args)
//    {
//        // System.out.println("Blockchain info: " + bitcoinService.blockchainInfo(BitcoinRequest.getBlockchainInfo()));
//
////        bitcoinService
////            .getBlockchainInfo(BitcoinRequests.getBlockchainInfo())
////            .subscribe().with(info -> System.out.println("Blockchain info: " + info));
//
//        Quarkus.waitForExit();
//        return 0;
//    }

    private void setup()
    {
        // ## Setup

        // 1. Initialize the FeeEstimator
        final FeeEstimator feeEstimator = FeeEstimator.new_impl(this::feeEstimator);

        // 2. Initialize the Logger
        final Logger logger = Logger.new_impl(x -> {});

        // 3. Initialize the BroadcasterInterface
        final BroadcasterInterface broadcaster = BroadcasterInterface.new_impl(this::broadcaster);

        // 4. Optional: Initialize the NetworkGraph
        // Skipped, not generating routes to send payments over

        // 5. Initialize Persist
        final Persist persist = Persist.new_impl(new ChannelMonitorPersist());

        // 6. Initialize the EventHandler
        // Delayed until ChannelManager is available to persist events...

        // 7. Optional: Initialize the Transaction Filter
        final Option_FilterZ filter = Option_FilterZ.none();

        // 8. Initialize the ChainMonitor
        final ChainMonitor chainMonitor = ChainMonitor.of(filter, broadcaster, logger, feeEstimator, persist);

        // 9. Initialize the KeysManager
        final long startupTime = effects.time();
        final byte[] seed = effects.seed();
        final KeysManager keysManager = KeysManager.of(
            seed
            , TimeUnit.MILLISECONDS.toSeconds(startupTime)
            , (int) TimeUnit.MILLISECONDS.toNanos(startupTime)
        );

        // 10. Read ChannelMonitors from disk
        final byte[][] channelMonitors = effects.readDirectory("monitors", Path.of("data"));

        bitcoinService
            .getBlockchainInfo(BitcoinRequests.getBlockchainInfo())
            // 11. Initialize the ChannelManager
            .onItem().transform(blockchainInfo ->
                this.channelManagerConstructor(blockchainInfo, channelMonitors, keysManager, chainMonitor, feeEstimator, broadcaster, logger)
            )
            // 12. Sync ChannelMonitors and ChannelManager to chain tip
            .onItem().transform(channelManagerConstructor ->
                this.syncToChainTip(channelManagerConstructor, chainMonitor)
            )
            .subscribe().with(x -> {
                // TODO
            });
    }

    private Object syncToChainTip(
        ChannelManagerConstructor channelManagerConstructor
        , ChainMonitor chainMonitor
    )
    {
        final ChannelManager channelManager = channelManagerConstructor.channel_manager;
        byte[][] channelManagerTxIds = channelManager.as_Confirm().get_relevant_txids();
        byte[][] chainTxIds = chainMonitor.as_Confirm().get_relevant_txids();
        final var allTxIds = new ArrayList<>(List.of(channelManagerTxIds));
        allTxIds.addAll(List.of(chainTxIds));
        for (byte[] txid : allTxIds)
        {
            
        }

        allTxIds.stream()
            .filter(txid -> !chainBackend.isConfirmed(txid))
            .forEach(txid -> {
                channelManager.as_Confirm().transaction_unconfirmed(txid);
                chainMonitor.as_Confirm().transaction_unconfirmed(txid);
            });

        checkBlockchain(relevantTxs, channelManager, chainMonitor);
        channelManagerConstructor.chain_sync_completed(channelManagerPersister);
        return null;  // TODO: Customise this generated block
    }

//    private Function<BlockchainInfo, ChannelManager> toChannelManager(
//        KeysManager keysManager
//        , FeeEstimator feeEstimator
//        , ChainMonitor chainMonitor
//        , BroadcasterInterface broadcaster
//        , Logger logger
//    )
//    {
//        return blockchainInfo ->
//        {
//
//        }
//    }

    private ChannelManagerConstructor channelManagerConstructor(
        BlockchainInfo blockchainInfo
        , byte[][] channelMonitors
        , KeysManager keysManager
        , ChainMonitor chainMonitor
        , FeeEstimator feeEstimator
        , BroadcasterInterface broadcaster
        , Logger logger
    )
    {
        final byte[] channelManagerBytes = effects.readAllBytes("manager", Path.of("data"));
        if (channelManagerBytes.length == 0)
        {
            return new ChannelManagerConstructor(
                Network.LDKNetwork_Regtest,
                UserConfig.with_default(),
                Hex.decode(blockchainInfo.result().bestBlockHash()),
                blockchainInfo.result().blocks(),
                keysManager.as_KeysInterface(),
                feeEstimator,
                chainMonitor,
                null,
                broadcaster,
                logger
            );
        }
        else
        {
            try
            {
                return new ChannelManagerConstructor(
                    channelManagerBytes,
                    channelMonitors,
                    UserConfig.with_default(),
                    keysManager.as_KeysInterface(),
                    feeEstimator,
                    chainMonitor,
                    null,
                    null,
                    broadcaster,
                    logger
                );
            }
            catch (ChannelManagerConstructor.InvalidSerializedDataException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void broadcaster(byte[] txBytes)
    {
        broadcastTx(new Transaction(NetworkParameters.fromID(ID_REGTEST), txBytes));
    }

    private void broadcastTx(Transaction tx)
    {
        System.out.printf("before broadcast txid %s%n", tx.getTxId());
        final var hexTx = Hex.toHexString(tx.bitcoinSerialize());
        bitcoinService
            .sendRawTransaction(BitcoinRequests.sendRawTransaction(hexTx))
            .subscribe().with(this::onBroadcastTx);
    }

    private void onBroadcastTx(SendRawTransaction sendRawTransaction)
    {
        System.out.printf("broadcast %s%n", sendRawTransaction.result().txId());
    }

    private int feeEstimator(ConfirmationTarget confirmationTarget)
    {
        return switch (confirmationTarget)
        {
            case LDKConfirmationTarget_Background -> 253;
            case LDKConfirmationTarget_Normal -> 2000; // (i.e. within ~6 blocks)
            case LDKConfirmationTarget_HighPriority -> 5000;
        };
    }
}
