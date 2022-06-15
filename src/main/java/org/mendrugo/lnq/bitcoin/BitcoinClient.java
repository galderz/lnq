package org.mendrugo.lnq.bitcoin;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Path("/")
@RegisterRestClient(configKey = "bitcoind")
@ClientHeaderParam(name = "Authorization", value = "{lookupAuth}")
@RegisterProvider(BitcoinResponseExceptionMapper.class)
public interface BitcoinClient
{
    @POST
    BlockchainInfo blockchainInfo(BitcoinRequests.StringParams req);

    @POST
    FundRawTransaction.Response fundRawTransaction(FundRawTransaction.Request req);

    @POST
    GetRawTransaction getRawTransaction(BitcoinRequests.StringParams req);

    @POST
    SendRawTransaction.Response sendRawTransaction(SendRawTransaction.Request req);

    @POST
    SignRawTransactionWithWallet signRawTransactionWithWallet(BitcoinRequest req);

    default String lookupAuth()
    {
        return "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
    }
}
