package org.mendrugo.lnq.ldk;

import org.bouncycastle.util.encoders.Hex;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.enums.Network;
import org.ldk.structs.UserConfig;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;
import org.mendrugo.lnq.bitcoin.BlockchainInfo;
import org.mendrugo.lnq.effects.Effects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Path;

@ApplicationScoped
public class LnChannelManagerConstructor
{
    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    @Inject
    LnChainMonitor chainMonitor;

    public ChannelManagerConstructor constructor;

    @Inject
    Effects effects;

    @Inject
    LnKeysManager keysManager;

    @PostConstruct
    void onStart()
    {
        System.out.println("LnChannelManagerConstructor.onStart()");
        final BlockchainInfo blockchainInfo = bitcoinService.blockchainInfo(BitcoinRequests.getBlockchainInfo());
        final byte[] channelManagerBytes = effects.readAllBytes("manager", Path.of("data"));
        final byte[][] channelMonitors = effects.readDirectory("monitors", Path.of("data"));
        if (channelManagerBytes.length == 0)
        {
            this.constructor = new ChannelManagerConstructor(
                Network.LDKNetwork_Regtest,
                UserConfig.with_default(),
                Hex.decode(blockchainInfo.result().bestBlockHash()),
                blockchainInfo.result().blocks(),
                keysManager.keysManager.as_KeysInterface(),
                // chainMonitor.feeEstimator.feeEstimator,
                null,
                chainMonitor.chainMonitor,
                null,
                chainMonitor.broadcaster.broadcaster,
                chainMonitor.logger.logger
            );
        }
        else
        {
            try
            {
                this.constructor = new ChannelManagerConstructor(
                    channelManagerBytes,
                    channelMonitors,
                    UserConfig.with_default(),
                    keysManager.keysManager.as_KeysInterface(),
                    // chainMonitor.feeEstimator.feeEstimator,
                    null,
                    chainMonitor.chainMonitor,
                    null,
                    null,
                    chainMonitor.broadcaster.broadcaster,
                    chainMonitor.logger.logger
                );
            }
            catch (ChannelManagerConstructor.InvalidSerializedDataException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
