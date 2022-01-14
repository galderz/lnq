package org.mendrugo.lnq.ldk;

import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.Logger;
import org.ldk.structs.Option_FilterZ;
import org.ldk.structs.Persist;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class ChainMonitorFactory
{
    @Inject
    BroadcasterInterface broadcaster;

    @Inject
    FeeEstimator feeEstimator;

    @Inject
    Logger logger;

    @Inject
    Persist persist;

    @Produces
    ChainMonitor chainMonitor()
    {
        return ChainMonitor.of(
            Option_FilterZ.none()
            , broadcaster
            , logger
            , feeEstimator
            , persist
        );
    }
}
