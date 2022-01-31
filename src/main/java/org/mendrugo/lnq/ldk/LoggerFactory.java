package org.mendrugo.lnq.ldk;

import org.ldk.structs.Logger;
import org.ldk.structs.Record;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class LoggerFactory implements Logger.LoggerInterface
{
    @Produces
    Logger logger()
    {
        return Logger.new_impl(this);
    }

    @Override
    public void log(Record record)
    {
        System.out.println("[LDK] " + record);
    }
}
