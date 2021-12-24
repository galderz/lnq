package org.mendrugo.lnq.node;

import org.mendrugo.lnq.effects.Effects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
public class Node implements Consumer<NodeArgs>
{
    @Inject
    Effects effects;

    @Override
    public void accept(NodeArgs nodeArgs)
    {
        System.out.println("Time: " + effects.time());
        System.out.println("Node args: " + nodeArgs);
    }
}
