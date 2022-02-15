package org.mendrugo.lnq.ldk;

import org.ldk.structs.Record;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class LoggerFactory implements org.ldk.structs.Logger.LoggerInterface
{
    @Inject
    org.jboss.logging.Logger log;

    @Produces
    org.ldk.structs.Logger logger()
    {
        return org.ldk.structs.Logger.new_impl(this);
    }

    @Override
    public void log(Record record)
    {
        log.infof(
            "[%s] %s"
            , record.get_level()
            , record.get_args()
        );
    }
}
