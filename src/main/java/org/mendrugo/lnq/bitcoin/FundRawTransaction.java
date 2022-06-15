package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public final class FundRawTransaction
{
    record Request(
        String jsonrpc
        , String id
        , String method
        , List<JsonNode> params
    ) {}

    // TODO can we make it non public?
    //   need to add static or default methods to BitcoinClient to avoid that
    public record Response(String id, Result result)
    {
        // TODO rename tx to rawTx
        public record Result(@JsonProperty("hex") String tx) {}
    }
}

