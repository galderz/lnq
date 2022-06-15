package org.mendrugo.lnq.ldk;

import org.ldk.structs.Record;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class LoggerFactory implements org.ldk.structs.Logger.LoggerInterface
{
    @Inject
    org.jboss.logging.Logger jbossLog;

    @Produces
    org.ldk.structs.Logger logger()
    {
        return org.ldk.structs.Logger.new_impl(this);
    }

    @Override
    public void log(Record record)
    {
        switch (record.get_level())
        {
            case LDKLevel_Debug ->
                jbossLog.debugf(
                    "[%s] %s"
                    , record.get_level()
                    , record.get_args()
                );
            case LDKLevel_Error ->
                jbossLog.errorf(
                    "[%s] %s"
                    , record.get_level()
                    , record.get_args()
                );
            case LDKLevel_Info ->
                jbossLog.infof(
                    "[%s] %s"
                    , record.get_level()
                    , record.get_args()
                );
            case LDKLevel_Trace ->
                jbossLog.tracef(
                    "[%s] %s"
                    , record.get_level()
                    , record.get_args()
                );
            case LDKLevel_Warn ->
                jbossLog.warnf(
                    "[%s] %s"
                    , record.get_level()
                    , record.get_args()
                );
        }
    }
}
