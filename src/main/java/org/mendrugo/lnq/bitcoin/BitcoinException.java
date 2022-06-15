package org.mendrugo.lnq.bitcoin;

final class BitcoinException extends RuntimeException
{
    final int errorCode;

    public BitcoinException(int errorCode, String message)
    {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String toString()
    {
        return String.format(
            "%s (error code: %d)"
            , super.toString()
            , errorCode
        );
    }
}
