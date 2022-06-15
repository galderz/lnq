package org.mendrugo.lnq.bitcoin;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.microprofile.client.DefaultResponseExceptionMapper;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;

@Priority(4000)
public class BitcoinResponseExceptionMapper implements ResponseExceptionMapper<Throwable>
{
    @Override
    public Throwable toThrowable(Response response)
    {
        if (response.getStatus() == 500)
        {
            response.bufferEntity();
            return response.readEntity(ErrorResponse.class).toException();
        }

        return new DefaultResponseExceptionMapper().toThrowable(response);
    }
}
