package org.mendrugo.lnq.bitcoin;

public record BlockchainInfo(
    String id
    , Result result
)
{
    public record Result(
        int blocks
        , String bestblockhash
    ) {}
}
