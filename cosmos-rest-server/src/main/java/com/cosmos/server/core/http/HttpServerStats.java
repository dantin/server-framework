package com.cosmos.server.core.http;

import com.cosmos.server.core.http.rest.URLResource;
import com.cosmos.server.core.http.rest.controller.URLController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Http server statistics.
 *
 * @author BSD
 */
public class HttpServerStats {

    // total missed request times
    static final AtomicLong requestMiss = new AtomicLong();
    // total hit request times
    static final AtomicLong requestHit = new AtomicLong();
    // total connections on current
    static final AtomicLong connections = new AtomicLong();
    // all URLResource mapping for hit count
    static Map<URLResource, URLController> resourceMap = new HashMap<>();
    // last regular service timestamp
    static volatile long lastServTime = System.currentTimeMillis();
    // last regular request id
    static volatile String lastServID;
    // last regular failed request id
    static volatile String lastServFailID;

    // stats switch
    private static boolean inUse = true;

    public static long getRequestMiss() {
        return requestMiss.longValue();
    }

    public static void incrRequestMiss() {
        if (inUse)
            requestMiss.incrementAndGet();
    }

    public static long getRequestHit() {
        return requestHit.longValue();
    }

    public static void incrRequestHit() {
        if (inUse)
            requestHit.incrementAndGet();
    }

    public static long getConnections() {
        return connections.longValue();
    }

    public static void incrConnections() {
        if (inUse)
            connections.incrementAndGet();
    }

    public static void decrConnections() {
        if (inUse)
            connections.decrementAndGet();
    }

    public static long getLastServTime() {
        return lastServTime;
    }

    public static void setLastServTime(long lastServTime) {
        if (inUse)
            HttpServerStats.lastServTime = lastServTime;
    }

    public static String getLastServID() {
        return lastServID;
    }

    public static void setLastServID(String lastServID) {
        if (inUse)
            HttpServerStats.lastServID = lastServID;
    }

    public static Map<URLResource, URLController> getResourcesMap() {
        return resourceMap;
    }

    public static void setResourceMap(Map<URLResource, URLController> resourceMap) {
        if (inUse)
            HttpServerStats.resourceMap = resourceMap;
    }

    public static String getLastServFailID() {
        return lastServFailID;
    }

    public static void setLastServFailID(String lastServFailID) {
        if (inUse)
            HttpServerStats.lastServFailID = lastServFailID;
    }

    public static void disable() {
        inUse = false;
    }

    public static void enable() {
        inUse = true;
    }
}
