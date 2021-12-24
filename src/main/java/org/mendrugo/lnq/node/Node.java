package org.mendrugo.lnq.node;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequest;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
public class Node implements Consumer<NodeArgs>
{
    @Inject
    Effects effects;

    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    @Override
    public void accept(NodeArgs nodeArgs)
    {
        System.out.println("Time: " + effects.time());
        System.out.println("Node args: " + nodeArgs);
        System.out.println("Blockchain info: " + bitcoinService.blockchainInfo(BitcoinRequest.getBlockchainInfo()));
    }
}
