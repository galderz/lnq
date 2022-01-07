package org.mendrugo.lnq.node;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.CommandLineArguments;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequest;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Arrays;

@ApplicationScoped
public class Node
{
    @Inject
    Effects effects;

    @Inject
    @RestClient
    BitcoinClient bitcoinService;

    @Inject
    @CommandLineArguments
    String[] args;

    void onStart(@Observes StartupEvent ev)
    {
        System.out.println("Startup Time: " + effects.time());
        System.out.println("Command line args: " + Arrays.toString(args));
        setup();
    }

    private void setup()
    {
        System.out.println("Blockchain info: " + bitcoinService.blockchainInfo(BitcoinRequest.getBlockchainInfo()));
    }
}
