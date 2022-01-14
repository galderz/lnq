package org.mendrugo.lnq.ldk;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.Persist;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;
import org.mendrugo.lnq.bitcoin.SendRawTransaction;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LnBroadcaster implements BroadcasterInterface.BroadcasterInterfaceInterface
{
    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    BroadcasterInterface broadcaster;

    @PostConstruct
    void onStart()
    {
        this.broadcaster = BroadcasterInterface.new_impl(this);
    }

    @Override
    public void broadcast_transaction(byte[] txBytes)
    {
        broadcastTx(new Transaction(NetworkParameters.fromID(NetworkParameters.ID_REGTEST), txBytes));
    }

    private void broadcastTx(Transaction tx)
    {
        System.out.printf("before broadcast txid %s%n", tx.getTxId());
        final SendRawTransaction sendRawTransaction = bitcoinService
            .sendRawTransaction(
                BitcoinRequests.sendRawTransaction(tx.bitcoinSerialize())
            );
        System.out.printf("broadcast %s%n", sendRawTransaction.result().txId());
    }
}
