package org.mendrugo.lnq.ldk;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.enums.ConfirmationTarget;
import org.ldk.structs.ChannelManager;
import org.ldk.structs.Event;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Result_TransactionNoneZ;
import org.ldk.structs.TxOut;
import org.mendrugo.lnq.bitcoin.BitcoinClient;
import org.mendrugo.lnq.bitcoin.BitcoinRequests;
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

    public EventHandler(BitcoinClient bitcoinService, ChannelManagerConstructor channelManagerConstructor, Effects effects, FeeEstimator feeEstimator, KeysManager keysManager) {
        this.bitcoinService = bitcoinService;
        this.channelManagerConstructor = channelManagerConstructor;
        this.effects = effects;
        this.feeEstimator = feeEstimator;
        this.keysManager = keysManager;
    }

    @Override
    public void handle_event(Event e)
    {
        final String refundAddress = "";
        final ChannelManager channelManager= channelManagerConstructor.channel_manager;
        final var params = NetworkParameters.fromID(ID_REGTEST);
        if (e instanceof Event.FundingGenerationReady event)
        {
            final var transaction = new Transaction(params);
            final var input = new TransactionInput(params, transaction, new byte[0]);
            input.setWitness(new TransactionWitness(2));
            input.getWitness().setPush(0, new byte[]{0x1});
            transaction.addInput(input);
            final var script = new Script(event.output_script);
            final var value = Coin.SATOSHI.multiply(event.channel_value_satoshis);
            transaction.addOutput(value, script);
            channelManager.funding_transaction_generated(event.temporary_channel_id, transaction.bitcoinSerialize());
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
}
