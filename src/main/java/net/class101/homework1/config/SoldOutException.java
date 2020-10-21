package net.class101.homework1.config;

public class SoldOutException extends RuntimeException {
    public int code;

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
