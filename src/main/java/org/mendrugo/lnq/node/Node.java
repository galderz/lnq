package org.mendrugo.lnq.node;

import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Node implements Runnable
{
    @Inject
    Effects effects;

    @Override
    public void run()
    {
        System.out.println("Time: " + effects.time());
    }
}
