package org.mendrugo.lnq.ldk;

import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.Option_FilterZ;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LnChainMonitor
{
    ChainMonitor chainMonitor;

    @Inject
    LnBroadcaster broadcaster;

    @Inject
    LnFeeEstimator feeEstimator;

    @Inject
    LnLogger logger;

    @Inject
    LnPersist persist;

    @PostConstruct
    void onStart()
    {
        this.chainMonitor = ChainMonitor.of(
            Option_FilterZ.none()
            , broadcaster.broadcaster
            , logger.logger
            // , feeEstimator.feeEstimator
            , null
            , persist.persist
        );
    }
}
