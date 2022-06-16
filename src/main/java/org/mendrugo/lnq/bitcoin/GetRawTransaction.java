package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

final class GetRawTransaction
{
    record Request(String jsonrpc, String id, String method, List<String> params) {}

    public record Response(String id, Result result)
    {
        record Result(@JsonProperty("hex") String tx) {}
    }
}

