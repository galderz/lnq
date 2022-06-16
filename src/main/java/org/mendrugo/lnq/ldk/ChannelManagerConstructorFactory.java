package org.mendrugo.lnq.ldk;

import org.bouncycastle.util.encoders.Hex;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.enums.Network;
import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Logger;
import org.ldk.structs.UserConfig;
import org.mendrugo.lnq.bitcoin.BitcoinService;
import org.mendrugo.lnq.bitcoin.BlockchainInfo;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.nio.file.Path;

public class ChannelManagerConstructorFactory
{
    @Inject
    BitcoinService bitcoinService;

    @Inject
    BroadcasterInterface broadcaster;

    @Inject
    ChainMonitor chainMonitor;

    @Inject
    Effects effects;

    @Inject
    FeeEstimator feeEstimator;

    @Inject
    KeysManager keysManager;

    @Inject
    Logger logger;

    @Produces
    ChannelManagerConstructor channelManagerConstructor()
    {
        System.out.println("ChannelManagerConstructorFactory.channelManagerConstructor()");
        final BlockchainInfo blockchainInfo = bitcoinService.getBlockchainInfo();
        final byte[] channelManagerBytes = effects.readAllBytes("manager", Path.of("data"));
        final byte[][] channelMonitors = effects.readDirectory("monitors", Path.of("data"));
        if (channelManagerBytes.length == 0)
        {
            return new ChannelManagerConstructor(
                Network.LDKNetwork_Regtest
                , UserConfig.with_default()
                , Hex.decode(blockchainInfo.bestBlockHash())
                , blockchainInfo.blocks()
                , keysManager.as_KeysInterface()
                , feeEstimator
                , chainMonitor
                , null // net graph
                , broadcaster
                , logger
            );
        }
        else
        {
            try
            {
                return new ChannelManagerConstructor(
                    channelManagerBytes
                    , channelMonitors
                    , UserConfig.with_default()
                    , keysManager.as_KeysInterface()
                    , feeEstimator
                    , chainMonitor
                    , null // filter
                    , null // net graph
                    , broadcaster
                    , logger
                );
            }
            catch (ChannelManagerConstructor.InvalidSerializedDataException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
