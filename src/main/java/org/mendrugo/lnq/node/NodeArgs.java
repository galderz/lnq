package org.mendrugo.lnq.node;

public record NodeArgs(
    String bitcoindRpcUser
    , String bitcoinRpcPassword
    , String bitcoinRpcHost
    , int bitcoinRpcPort
) {}
