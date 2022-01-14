package org.mendrugo.lnq.ldk;

import org.ldk.enums.ConfirmationTarget;
import org.ldk.structs.FeeEstimator;

import javax.enterprise.inject.Produces;

public class FeeEstimatorFactory implements FeeEstimator.FeeEstimatorInterface
{
    @Produces
    FeeEstimator feeEstimator()
    {
        return FeeEstimator.new_impl(this);
    }

    @Override
    public int get_est_sat_per_1000_weight(ConfirmationTarget confirmationTarget)
    {
        return switch (confirmationTarget)
        {
            case LDKConfirmationTarget_Background -> 253;
            case LDKConfirmationTarget_Normal -> 2000; // (i.e. within ~6 blocks)
            case LDKConfirmationTarget_HighPriority -> 5000;
        };
    }
}
