package org.mendrugo.lnq.bitcoin;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Path("/")
@RegisterRestClient(configKey = "bitcoind")
@ClientHeaderParam(name = "Authorization", value = "{lookupAuth}")
public interface BitcoinClient
{
    @POST
    Uni<BlockchainInfo> getBlockchainInfo(BitcoinRequest req);

    @POST
    Uni<SendRawTransaction> sendRawTransaction(BitcoinRequest req);

    default String lookupAuth()
    {
        return "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));
    }
}
