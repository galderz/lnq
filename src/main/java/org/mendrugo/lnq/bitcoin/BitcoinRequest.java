package org.mendrugo.lnq.bitcoin;

import java.util.List;

public record BitcoinRequest(
    String jsonrpc
    , String id
    , String method
    , List<String> params
) {}
