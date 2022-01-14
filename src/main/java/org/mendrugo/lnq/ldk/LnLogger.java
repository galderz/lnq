package org.mendrugo.lnq.ldk;

import org.ldk.structs.Logger;
import org.ldk.structs.Record;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LnLogger implements Logger.LoggerInterface
{
    Logger logger;

    @PostConstruct
    void onStart()
    {
        this.logger = Logger.new_impl(this);
    }

    @Override
    public void log(Record record)
    {
        System.out.println("[LDK] " + record);
    }
}
