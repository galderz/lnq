package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

final class FundRawTransaction
{
    record Request(String jsonrpc, String id, String method, List<JsonNode> params) {}

    public record Response(String id, Result result)
    {
        record Result(@JsonProperty("hex") String rawTx) {}
    }
}

