package org.mendrugo.lnq.effects;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Effects
{
    public long time()
    {
        return System.currentTimeMillis();
    }
}
