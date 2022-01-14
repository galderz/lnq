package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetRawTransaction(String id, Result result)
{
    public record Result(@JsonProperty("hex") String txId) {}
}
