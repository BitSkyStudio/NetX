package com.github.industrialcraft.netx.timeout;

public class TimeoutException extends RuntimeException{
    public TimeoutException() {
    }
    public TimeoutException(String message) {
        super(message);
    }
}
