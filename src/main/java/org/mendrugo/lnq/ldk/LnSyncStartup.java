package org.mendrugo.lnq.ldk;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.ldk.structs.ChannelManager;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LnSyncStartup
{
    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    @Inject
    LnChainMonitor chainMonitor;

    @Inject
    LnChannelManagerConstructor channelManagerConstructor;

    @Inject
    LnEventHandler eventHandler;

    public boolean completed;

    @PostConstruct
    void onStart()
    {
        System.out.println("ENTRY LnSyncStartup.onStart()");
        final ChannelManager channelManager = channelManagerConstructor.constructor.channel_manager;
        byte[][] channelManagerTxIds = channelManager.as_Confirm().get_relevant_txids();
        byte[][] chainTxIds = chainMonitor.chainMonitor.as_Confirm().get_relevant_txids();
        final var allTxIds = new ArrayList<>(List.of(channelManagerTxIds));
        allTxIds.addAll(List.of(chainTxIds));
        allTxIds.stream()
            .filter(txid -> !isConfirmed(txid))
            .forEach(txid ->
            {
                channelManager.as_Confirm().transaction_unconfirmed(txid);
                chainMonitor.chainMonitor.as_Confirm().transaction_unconfirmed(txid);
            });

        channelManagerConstructor.constructor.chain_sync_completed(eventHandler, null);
        completed = true;
    }

    private boolean isConfirmed(byte[] txid)
    {
        return bitcoinService.getRawTransaction(BitcoinRequests.getRawTransaction(txid)).result() != null;
    }
}
