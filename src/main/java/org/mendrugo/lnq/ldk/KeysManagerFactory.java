package org.mendrugo.lnq.ldk;

import org.ldk.structs.KeysManager;
import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class KeysManagerFactory
{
    @Inject
    Effects effects;

    @Produces
    KeysManager onStart()
    {
        final long startupTime = effects.time();
        return KeysManager.of(
            effects.seed()
            , TimeUnit.MILLISECONDS.toSeconds(startupTime)
            , (int) TimeUnit.MILLISECONDS.toNanos(startupTime)
        );
    }
}
