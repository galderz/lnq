package org.mendrugo.lnq.node;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.CommandLineArguments;
import org.mendrugo.lnq.effects.Effects;
import org.mendrugo.lnq.ldk.LnFeeEstimator;
import org.mendrugo.lnq.ldk.LnSyncStartup;

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
    LnSyncStartup lnSyncStartup;

    @Inject
    LnFeeEstimator feeEstimator;

    @Inject
    @CommandLineArguments
    String[] args;

    void onStart(@Observes StartupEvent ev)
    {
        System.out.println("Startup Time: " + effects.time());
        System.out.println("Command line args: " + Arrays.toString(args));
        // System.out.println("LNQ node sync completed " + lnSyncStartup.completed);
        System.out.println("LNQ node fee estimator " + feeEstimator);
    }
}
