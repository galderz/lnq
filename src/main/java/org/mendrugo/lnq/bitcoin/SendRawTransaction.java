package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class SendRawTransaction
{
    record Request(
        String jsonrpc
        , String id
        , String method
        , List<String> params
    ) {}

    public record Response(String id, @JsonProperty("result") String txId) {}
}

