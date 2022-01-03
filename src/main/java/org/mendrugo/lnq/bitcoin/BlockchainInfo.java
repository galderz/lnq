package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BlockchainInfo(
    String id
    , Result result
)
{
    public record Result(
        int blocks
        , @JsonProperty("bestblockhash") String bestBlockHash
    ) {}
}
