package org.mendrugo.lnq.ldk;

import org.ldk.enums.ConfirmationTarget;
import org.ldk.structs.FeeEstimator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LnFeeEstimator implements FeeEstimator.FeeEstimatorInterface
{
    public FeeEstimator feeEstimator;

    @PostConstruct
    public void onStartup()
    {
        System.out.println("ENTRY LnFeeEstimator.onStartup");
        this.feeEstimator = FeeEstimator.new_impl(this);
        System.out.println("EXIT LnFeeEstimator.onStartup");
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
