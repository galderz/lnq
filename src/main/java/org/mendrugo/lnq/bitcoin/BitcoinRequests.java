package org.mendrugo.lnq.bitcoin;

import org.bouncycastle.util.encoders.Hex;

import java.util.List;

public class BitcoinRequests
{
    public static BitcoinRequest getRawTransaction(byte[] txid)
    {
        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "sendrawtransaction"
            , List.of(Hex.toHexString(reverse(txid)), "true")
        );
    }

    public static BitcoinRequest sendRawTransaction(byte[] tx)
    {
        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "sendrawtransaction"
            , List.of(Hex.toHexString(tx))
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

    private static byte[] reverse(final byte[] input)
    {
        final var output = new byte[input.length];
        for (var i = 0; i < input.length; i++)
        {
            output[input.length - i - 1] = input[i];
        }
        return output;
    }

    private BitcoinRequests() {}
}
