package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.bouncycastle.util.encoders.Hex;

import java.util.List;

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

    public static StringParams getRawTransaction(byte[] txid)
    {
        return new StringParams(
            "1.0"
            , "curltest"
            , "sendrawtransaction"
            , List.of(Hex.toHexString(reverse(txid)), "true")
        );
    }

    public static SendRawTransaction.Request sendRawTransaction(byte[] tx)
    {
        return new SendRawTransaction.Request(
            "1.0"
            , "curltest"
            , "sendrawtransaction"
            , List.of(Hex.toHexString(tx))
        );
    }

    public static BitcoinRequest signRawTransactionWithWallet(String tx)
    {
        return new BitcoinRequest(
            "1.0"
            , "curltest"
            , "signrawtransactionwithwallet"
            , List.of(tx)
        );
    }

    public static StringParams getBlockchainInfo()
    {
        return new StringParams(
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

    public record StringParams(
        String jsonrpc
        , String id
        , String method
        , List<String> params
    ) {}

    public record OutputParams(
        String jsonrpc
        , String id
        , String method
        , List<List<Output>> params
    ) {}

    record Output(String data) {}

    private BitcoinRequests() {}
}
