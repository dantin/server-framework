package com.cosmos.server.core.rest;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Base class for a http context, similar to <code>javax.servlet</code> and SpringMVC <code>HttpContext</code>
 * implementation parameter.
 * <p>
 * It can be used for migration code from SpringMVC.
 *
 * @author BSD
 */
public abstract class HttpSession {

    // client request with unique id, fetch it from http header(Request-Id)
    // if exist. or generate by UUID
    private long creationTime = System.currentTimeMillis();

    public HttpSession() {
        // do nothing here
    }

    public String getId() {
        return getRequestId();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessedTime() {
        // TODO : this time will updated by long connection
        return creationTime;
    }

    public int getMaxInactiveInterval() {
        throw new NotImplementedException();
    }

    public void setMaxInactiveInterval(int interval) {
        throw new NotImplementedException();
    }

    public Object getAttribute(String name) {
        Object value;
        if ((value = getHttpParams().get(name)) != null)
            return value;
        if ((value = getHttpAttributes().get(name)) != null)
            return value;
        return null;
    }

    public Object getValue(String name) {
        return getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return new Enumeration<String>() {
            Iterator<String> first = getHttpParams().keySet().iterator();
            Iterator<String> second = getHttpAttributes().keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return first.hasNext() || second.hasNext();
            }

            @Override
            public String nextElement() {
                try {
                    return first.next();
                } catch (NoSuchElementException e) {
                    return second.next();
                }
            }
        };
    }

    public String[] getValueNames() {
        String[] arr = new String[getHttpParams().keySet().size() + getHttpAttributes().keySet().size()];
        Enumeration<String> names = getAttributeNames();
        int index = 0;
        while (names.hasMoreElements())
            arr[index++] = names.nextElement();
        return arr;
    }

    public void setAttribute(String name, Object value) {
        getHttpAttributes().put(name, value);
    }

    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        getHttpAttributes().remove(name);
        getHttpParams().remove(name);
    }

    public void removeValue(String name) {
        removeAttribute(name);
    }

    public void invalidate() {
        getHttpAttributes().clear();
    }

    public boolean isNew() {
        return true;
    }

    public abstract String getRequestId();

    public abstract Map<String, Object> getHttpAttributes();

    public abstract Map<String, String> getHttpParams();
}
