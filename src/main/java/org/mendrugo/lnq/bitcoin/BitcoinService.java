package org.mendrugo.lnq.bitcoin;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BitcoinService
{
    @Inject
    @RestClient
    BitcoinClient bitcoinClient;

    /**
     * Fund a transaction.
     *
     * @param rawTx raw transaction bytes
     * @param feeRate fee rate in satoshis per vB
     * @return hex encoded funded raw transaction
     */
    public String fundRawTransaction(byte[] rawTx, int feeRate)
    {
        return bitcoinClient
            .fundRawTransaction(BitcoinRequests.fundRawTransaction(rawTx, feeRate))
            .result().rawTx();
    }

    /**
     * Get blockchain info.
     *
     * @return an instance of {@link BlockchainInfo} with blockchain information
     */
    public BlockchainInfo getBlockchainInfo()
    {
        return bitcoinClient
            .getBlockchainInfo(BitcoinRequests.getBlockchainInfo())
            .toBlockchainInfo();
    }

    /**
     * Get raw transaction.
     *
     * @param txId transaction id
     * @return hex encoded raw transaction or null if not found
     */
    public String getRawTransaction(byte[] txId)
    {
        return bitcoinClient
            .getRawTransaction(BitcoinRequests.getRawTransaction(txId))
            .result().tx();
    }

    /**
     * Sends a raw transaction.
     * 
     * @param rawTx raw transaction bytes
     * @return hex encoded transaction id
     */
    public String sendRawTransaction(byte[] rawTx)
    {
        return bitcoinClient
            .sendRawTransaction(BitcoinRequests.sendRawTransaction(rawTx))
            .txId();
    }

    /**
     * Sign a raw transaction.
     *
     * @param rawTx raw transaction bytes
     * @return signed transaction
     */
    public String signRawTransactionWithWallet(String rawTx)
    {
        SignRawTransactionWithWallet.Response.Result result = bitcoinClient
            .signRawTransactionWithWallet(BitcoinRequests.signRawTransactionWithWallet(rawTx))
            .result();

        if (!result.complete()) {
            throw new BitcoinException(-1, "ERROR: Transaction signatures not complete");
        }

        return result.tx();
    }

}
