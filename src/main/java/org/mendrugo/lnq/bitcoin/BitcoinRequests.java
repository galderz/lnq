package org.mendrugo.lnq.bitcoin;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bouncycastle.util.encoders.Hex;
import org.ldk.structs.Result_TransactionNoneZ;

import java.util.List;

public class BitcoinRequests
{
    public static BitcoinRequest createRawTransaction()
    {
        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "createrawtransaction"
            , List.of()
        );
    }

    public static BitcoinRequest getRawTransaction(byte[] txid)
    {
        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "getrawtransaction"
            , List.of(Hex.toHexString(reverse(txid)), "true")
        );
    }

    public static BitcoinRequest sendRawTransaction(byte[] txBytes)
    {
        final Transaction jTx = new Transaction(NetworkParameters.fromID(NetworkParameters.ID_REGTEST), txBytes);
        System.out.println("Tx id: " + jTx.getTxId());
        System.out.println("Tx to hex string: " + jTx.toHexString());
        System.out.println("Tx to string: " + jTx.toString());
        final String tx = Hex.toHexString(jTx.bitcoinSerialize());
        // final String rawTx = String.format("0%s", tx);

        final String rawTx = tx;
        System.out.println("Send raw transaction: " + rawTx);

        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "sendrawtransaction"
            , List.of(rawTx)
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
