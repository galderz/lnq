package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bouncycastle.util.encoders.Hex;

import java.util.List;

// TODO need this? it could be all in BitcoinService
public class BitcoinRequests
{
    public static FundRawTransaction.Request fundRawTransaction(byte[] tx, int feeRate)
    {
        ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("fee_rate", feeRate);
        objectNode.put("replaceable", false);

        return new FundRawTransaction.Request(
            "1.0"
            , "curltest"
            , "fundrawtransaction"
            , List.of(
                TextNode.valueOf(Hex.toHexString(tx))
                , objectNode
            )
        );
    }

    public static GetRawTransaction.Request getRawTransaction(byte[] txid)
    {
        return new GetRawTransaction.Request(
            "1.0"
            , "curltest"
            , "getrawtransaction"
            , List.of(Hex.toHexString(reverse(txid)), "true")
        );
    }

    public static SendRawTransaction.Request sendRawTransaction(byte[] rawTx)
    {
        return new SendRawTransaction.Request(
            "1.0"
            , "curltest"
            , "sendrawtransaction"
            , List.of(Hex.toHexString(rawTx))
        );
    }

    public static SignRawTransactionWithWallet.Request signRawTransactionWithWallet(String tx)
    {
        return new SignRawTransactionWithWallet.Request(
            "1.0"
            , "curltest"
            , "signrawtransactionwithwallet"
            , List.of(tx)
        );
    }

    public static GetBlockchainInfo.Request getBlockchainInfo()
    {
        return new GetBlockchainInfo.Request(
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
