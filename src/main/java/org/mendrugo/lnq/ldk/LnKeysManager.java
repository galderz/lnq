package org.mendrugo.lnq.ldk;

import org.ldk.structs.KeysManager;
import org.mendrugo.lnq.effects.Effects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class LnKeysManager
{
    @Inject
    Effects effects;

    KeysManager keysManager;

    final byte[] seed = effects.seed();

    final long startupTime = effects.time();

    @PostConstruct
    void onStart()
    {
        this.keysManager = KeysManager.of(
            seed
            , TimeUnit.MILLISECONDS.toSeconds(startupTime)
            , (int) TimeUnit.MILLISECONDS.toNanos(startupTime)
        );
    }
}
