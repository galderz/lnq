package org.mendrugo.lnq.bitcoin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

final class GetBlockchainInfo
{
    record Request(String jsonrpc, String id, String method, List<String> params) {}

    public record Response(String id, Result result)
    {
        BlockchainInfo toBlockchainInfo()
        {
            return new BlockchainInfo(result.blocks(), result.bestBlockHash);
        }
    }

    record Result(int blocks, @JsonProperty("bestblockhash") String bestBlockHash) {}
}
