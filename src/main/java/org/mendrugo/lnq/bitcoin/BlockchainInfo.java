package org.mendrugo.lnq.bitcoin;

public record BlockchainInfo(
    int blocks
    , String bestblockhash
) {}
