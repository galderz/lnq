package org.mendrugo.lnq.node;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
//import org.eclipse.microprofile.rest.client.inject.RestClient;
//import org.mendrugo.lnq.bitcoin.BitcoinClient;
//import org.mendrugo.lnq.bitcoin.BitcoinRequest;
import org.mendrugo.lnq.effects.Effects;

import javax.inject.Inject;

public class Node implements QuarkusApplication
{
    @Inject
    Effects effects;

//    @Inject
//    @RestClient
//    BitcoinClient bitcoinService;

    @Override
    public int run(String... args)
    {
        System.out.println("Do startup logic here");
        System.out.println("Time: " + effects.time());
//        System.out.println("Blockchain info: " + bitcoinService.blockchainInfo(BitcoinRequest.getBlockchainInfo()));
        Quarkus.waitForExit();
        return 0;
    }
}
