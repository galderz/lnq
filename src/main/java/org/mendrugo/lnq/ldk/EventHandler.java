package org.mendrugo.lnq.ldk;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;
import org.jboss.logging.Logger;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.enums.ConfirmationTarget;
import org.ldk.structs.APIError;
import org.ldk.structs.ChannelManager;
import org.ldk.structs.Event;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Result_NoneAPIErrorZ;
import org.ldk.structs.Result_TransactionNoneZ;
import org.ldk.structs.TxOut;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;
import org.mendrugo.lnq.bitcoin.FundRawTransaction;
import org.mendrugo.lnq.bitcoin.SignRawTransactionWithWallet;
import org.mendrugo.lnq.effects.Effects;

import java.io.IOException;
import java.nio.file.Path;

import static org.bitcoinj.core.NetworkParameters.ID_REGTEST;

public class EventHandler implements ChannelManagerConstructor.EventHandler
{
    final BitcoinClient bitcoinService;
    final ChannelManagerConstructor channelManagerConstructor;
    final Effects effects;
    final FeeEstimator feeEstimator;
    final KeysManager keysManager;
    final ClassLoader tccl;
    final Logger jbossLog;

    public EventHandler(BitcoinClient bitcoinService, ChannelManagerConstructor channelManagerConstructor, Effects effects, FeeEstimator feeEstimator, KeysManager keysManager, Logger jbossLog) {
        this.bitcoinService = bitcoinService;
        this.channelManagerConstructor = channelManagerConstructor;
        this.effects = effects;
        this.feeEstimator = feeEstimator;
        this.keysManager = keysManager;
        this.tccl = Thread.currentThread().getContextClassLoader();
        this.jbossLog = jbossLog;
    }

    @Override
    public void handle_event(Event e)
    {
        // Restore correct classloader to avoid class loading issues
        Thread.currentThread().setContextClassLoader(tccl);

        final String refundAddress = "";
        final ChannelManager channelManager= channelManagerConstructor.channel_manager;
        final NetworkParameters params = NetworkParameters.fromID(ID_REGTEST);
        if (e instanceof Event.FundingGenerationReady event)
        {
            jbossLog.info("EVENT: funding generation ready");

            final Transaction transaction = new Transaction(params);
            final Script script = new Script(event.output_script);
            final Coin value = Coin.SATOSHI.multiply(event.channel_value_satoshis);
            transaction.addOutput(value, script);

//            final CreateRawTransaction rawTx = bitcoinService.createRawTransaction(BitcoinRequests.createRawTransaction(transaction.bitcoinSerialize()));

            // Have your wallet put the inputs into the transaction
            // such that the output is satisfied.
            int feeRate = getEstimatedSatPer100Weight(ConfirmationTarget.LDKConfirmationTarget_Normal) / 250;
            final FundRawTransaction.Response fundedTx = bitcoinService.fundRawTransaction(BitcoinRequests.fundRawTransaction(transaction.bitcoinSerialize(), feeRate));

            // Sign the final funding transaction and broadcast it.
            final SignRawTransactionWithWallet signedTx = bitcoinService.signRawTransactionWithWallet(BitcoinRequests.signRawTransactionWithWallet(fundedTx.result().tx()));
            if (!signedTx.result().complete()) {
                System.out.println("ERROR: Transaction signatures not complete");
                return;
            }

            final Result_NoneAPIErrorZ result = channelManager.funding_transaction_generated(event.temporary_channel_id, Hex.decode(signedTx.result().tx()));
            if (!result.is_ok())
            {
                final Result_NoneAPIErrorZ.Result_NoneAPIErrorZ_Err error = (Result_NoneAPIErrorZ.Result_NoneAPIErrorZ_Err) result;
                if (error.err instanceof APIError.APIMisuseError apiMisuseError) {
                    System.out.println("ERROR: Unable to fund channel. API misuse error: " + apiMisuseError.err);
                } else {
                    System.out.println("ERROR: Unable to fund channel. Error is: " + error.err);
                }
                return;
            }
        }
        else if (e instanceof Event.PaymentReceived event)
        {
            System.out.printf("Payment of %s SAT received.%n", event.amt);
            channelManager.claim_funds(event.payment_hash);
        }
        else if (e instanceof Event.PaymentSent event)
        {
            System.out.printf("Payment with preimage '%s' sent.%n", Hex.toHexString(event.payment_preimage));
        }
        else if (e instanceof Event.PaymentPathFailed event)
        {
            System.out.printf("Payment path with payment hash '%s' failed.%n", Hex.toHexString(event.payment_hash));
        }
        else if (e instanceof Event.PendingHTLCsForwardable event)
        {
            channelManager.process_pending_htlc_forwards();
        }
        else if (e instanceof Event.SpendableOutputs event)
        {
            final var tx = keysManager.spend_spendable_outputs(
                event.outputs,
                new TxOut[]{},
                Hex.decode(refundAddress),
                feeEstimator.get_est_sat_per_1000_weight(ConfirmationTarget.LDKConfirmationTarget_HighPriority)
            );
            if (tx instanceof Result_TransactionNoneZ.Result_TransactionNoneZ_OK)
            {
                bitcoinService.sendRawTransaction(BitcoinRequests.sendRawTransaction(((Result_TransactionNoneZ.Result_TransactionNoneZ_OK) tx).res));
            }
        }
    }

    private int getEstimatedSatPer100Weight(ConfirmationTarget confirmationTarget)
    {
        return switch (confirmationTarget) {
            case LDKConfirmationTarget_Background -> 253;
            case LDKConfirmationTarget_Normal -> 2000;
            case LDKConfirmationTarget_HighPriority -> 5000;
        };
    }

    @Override
    public void persist_manager(byte[] bytes)
    {
        try
        {
            effects.persist(bytes, Path.of("manager"), Path.of("data"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void persist_network_graph(byte[] bytes)
    {
        try
        {
            effects.persist(bytes, Path.of("network"), Path.of("data"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
