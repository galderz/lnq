package org.mendrugo.lnq.bitcoin;

public record ErrorResponse(String result, Error error)
{
    public record Error(int code, String message, String id) {}

    BitcoinException toException()
    {
        return new BitcoinException(error().code(), error().message());
    }
}
