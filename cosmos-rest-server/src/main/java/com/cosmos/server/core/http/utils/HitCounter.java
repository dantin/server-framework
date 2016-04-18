package com.cosmos.server.core.http.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Service hit counter.
 *
 * @author BSD
 */
public class HitCounter {

    private final AtomicLong counter = new AtomicLong();

    /**
     * Atomically increase the counter.
     *
     * @return increased hit count
     */
    public long hit() {
        return counter.incrementAndGet();
    }

    /**
     * Get the current counter value.
     *
     * @return the current count value
     */
    public long count() {
        return counter.get();
    }
}
