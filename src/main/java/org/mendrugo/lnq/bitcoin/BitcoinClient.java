//package org.mendrugo.lnq.bitcoin;
//
//import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
//import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
//
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//@Path("/")
//@RegisterRestClient(configKey = "bitcoind")
//@ClientHeaderParam(name = "Authorization", value = "{lookupAuth}")
//public interface BitcoinClient
//{
//    @POST
//    BlockchainInfo blockchainInfo(BitcoinRequest req);
//
//    default String lookupAuth()
//    {
//        return "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
//    }
//}
