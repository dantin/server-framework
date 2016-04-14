package com.cosmos.data.redis;

/**
 * Redis related exception.
 *
 * @author BSD
 */
public class RedisException extends RuntimeException {

    /**
     * Constructs a new redis exception with the specified detail message.
     *
     * @param message detail message
     */
    public RedisException(String message) {
        super(message);
    }

    /**
     * Constructs a new redis exception with input parameters.
     *
     * @param message detail message
     * @param cause cause
     */
    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }
}
