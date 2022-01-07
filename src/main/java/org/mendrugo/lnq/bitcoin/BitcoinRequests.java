package org.mendrugo.lnq.bitcoin;

import java.util.List;

public final class BitcoinRequests
{
    public static BitcoinRequest sendRawTransaction(String tx)
    {
        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "getblockchaininfo"
            , List.of(tx)
        );
    }

    public static BitcoinRequest getBlockchainInfo()
    {
        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "getblockchaininfo"
            , List.of()
        );
    }

    private BitcoinRequests() {}
}
