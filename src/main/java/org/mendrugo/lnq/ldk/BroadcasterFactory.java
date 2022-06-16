package org.mendrugo.lnq.ldk;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.ldk.structs.BroadcasterInterface;
import org.mendrugo.lnq.bitcoin.BitcoinService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class BroadcasterFactory implements BroadcasterInterface.BroadcasterInterfaceInterface
{
    @Inject
    BitcoinService bitcoinService;

    @Inject
    org.jboss.logging.Logger jbossLog;

    @Produces
    BroadcasterInterface broadcaster()
    {
        return BroadcasterInterface.new_impl(this);
    }

    @Override
    public void broadcast_transaction(byte[] txBytes)
    {
        broadcastTx(new Transaction(NetworkParameters.fromID(NetworkParameters.ID_REGTEST), txBytes));
    }

    private void broadcastTx(Transaction tx)
    {
        jbossLog.infof("Before broadcast txid %s%n", tx.getTxId());
        jbossLog.debugf("Before broadcast rawTx %s", tx);
        final String txId = bitcoinService.sendRawTransaction(tx.bitcoinSerialize());
        jbossLog.infof("Broadcast %s%n", txId);
    }
}
