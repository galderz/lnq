//package org.mendrugo.lnq.bitcoin;
//
//import java.util.List;
//
//public record BitcoinRequest(
//    String jsonrpc
//    , String id
//    , String method
//    , List<String> params
//)
//{
//    public static BitcoinRequest getBlockchainInfo()
//    {
//        return new BitcoinRequest(
//            "1.0"
//            , "curltest"
//            , "getblockchaininfo"
//            , List.of()
//        );
//    }
//}
