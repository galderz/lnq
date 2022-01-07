package org.mendrugo.lnq.node;

import org.bouncycastle.util.encoders.Hex;
import org.ldk.structs.ChannelMonitor;
import org.ldk.structs.ChannelMonitorUpdate;
import org.ldk.structs.MonitorUpdateId;
import org.ldk.structs.OutPoint;
import org.ldk.structs.Persist.PersistInterface;
import org.ldk.structs.Result_NoneChannelMonitorUpdateErrZ;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

import static org.ldk.enums.ChannelMonitorUpdateErr.LDKChannelMonitorUpdateErr_PermanentFailure;
import static org.ldk.structs.Result_NoneChannelMonitorUpdateErrZ.err;
import static org.ldk.structs.Result_NoneChannelMonitorUpdateErrZ.ok;

@ApplicationScoped
public class ChannelMonitorPersist implements PersistInterface
{
    @Inject
    Effects effects;
    
    @Override
    public Result_NoneChannelMonitorUpdateErrZ persist_new_channel(
        OutPoint channelId
        , ChannelMonitor data
        , MonitorUpdateId updateId
    )
    {
        return persist(channelId, data.write());
    }

    @Override
    public Result_NoneChannelMonitorUpdateErrZ update_persisted_channel(
        OutPoint channelId
        , ChannelMonitorUpdate update
        , ChannelMonitor data
        , MonitorUpdateId updateId
    )
    {
        return persist(channelId, data.write());
    }

    private Result_NoneChannelMonitorUpdateErrZ persist(OutPoint id, byte[] data)
    {
        try
        {
            final var filename = String.format(
                "%s_%d"
                , Hex.toHexString(id.get_txid())
                , id.get_index()
            );
            effects.persist(data, Path.of("monitors", filename), Path.of("data"));
            return ok();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return err(LDKChannelMonitorUpdateErr_PermanentFailure);
        }
    }
}
