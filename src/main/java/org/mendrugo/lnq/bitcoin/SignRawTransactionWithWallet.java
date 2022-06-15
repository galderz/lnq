package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignRawTransactionWithWallet(String id, Result result)
{
    public record Result(@JsonProperty("hex") String tx, boolean complete) {}
}
