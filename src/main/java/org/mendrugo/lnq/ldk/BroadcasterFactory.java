package org.mendrugo.lnq.ldk;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.ldk.structs.BroadcasterInterface;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;
import org.mendrugo.lnq.bitcoin.SendRawTransaction;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class BroadcasterFactory implements BroadcasterInterface.BroadcasterInterfaceInterface
{
    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    @Produces
    BroadcasterInterface broadcaster()
    {
        return BroadcasterInterface.new_impl(this);
    }

    @Override
    public void broadcast_transaction(byte[] txBytes)
    {
        System.out.println("before broadcast...");
        final SendRawTransaction result = bitcoinService.sendRawTransaction(BitcoinRequests.sendRawTransaction(txBytes));
        System.out.printf("broadcast %s%n", result.result().txId());
    }
}
