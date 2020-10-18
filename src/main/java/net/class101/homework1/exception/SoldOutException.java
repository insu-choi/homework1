package net.class101.homework1.exception;

public class SoldOutException extends RuntimeException {
    public int code;

    public SoldOutException(int code)
    {
        this.code = code;
    }

    public SoldOutException(String message)
    {
        super(message);
        this.code = 1001;
    }

    public SoldOutException(int code, String message)
    {
        super(message);
        this.code = code;
    }

}
