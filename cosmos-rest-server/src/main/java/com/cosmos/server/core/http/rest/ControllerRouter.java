package com.cosmos.server.core.http.rest;

import com.cosmos.server.commons.constant.http.RequestMethod;
import com.cosmos.server.core.http.rest.controller.URLController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resource Controller router.
 *
 * @author BSD
 */
public class ControllerRouter {

    // default router size
    private static final int DEFAULT_ROUTER_SIZE = 512;

    // mapping between URLResource and URLController
    // first index layer. indexed by HttpMethod and URL terms length our max supported URL terms is 512
    private final URLMapper[][] mapper = new URLMapper[RequestMethod.UNKOWN.ordinal()][DEFAULT_ROUTER_SIZE];

    /**
     * Constructor
     */
    public ControllerRouter() {
        // do nothing here
    }

    /**
     * Get specified resource's {@link URLController}.
     *
     * @param resource analyzed url resource
     * @return handler instance or null if it don't exist
     */
    public URLController findURLController(URLResource resource) {
        try {
            // we have checked the request method is supported!. couldn't index overflow
            URLMapper slot = mapper[resource.requestMethod().ordinal()][resource.fragments().size()];
            if (slot != null)
                return slot.get(resource);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    /**
     * Register Resource and Controller into router
     *
     * @param resource url resource
     * @param controller url handler
     * @return true if success, false otherwise
     */
    public synchronized boolean register(URLResource resource, URLController controller) {
        Indexer indexer = new Indexer(resource);
        if (mapper[indexer.HttpMethodIndex][indexer.TermsIndex] == null)
            mapper[indexer.HttpMethodIndex][indexer.TermsIndex] = new URLMapper();
        return mapper[indexer.HttpMethodIndex][indexer.TermsIndex].register(resource, controller);
    }

    public synchronized void unregister(URLResource resource) {
        Indexer indexer = new Indexer(resource);
        if (mapper[indexer.HttpMethodIndex][indexer.TermsIndex] != null)
            mapper[indexer.HttpMethodIndex][indexer.TermsIndex].unregister(resource);
    }

    static class Indexer {
        public int HttpMethodIndex;
        public int TermsIndex;

        public Indexer(URLResource resource) {
            HttpMethodIndex = resource.requestMethod().ordinal();
            TermsIndex = resource.fragments().size();
        }
    }

    static class URLMapper {
        private final Map<URLResource, URLController> controller = new ConcurrentHashMap<>(256);


        public URLController get(URLResource resource) {
            return controller.get(resource);

        }

        public boolean register(URLResource resource, URLController controller) {
            return this.controller.put(resource, controller) == null;

        }

        public void unregister(URLResource resource) {
            controller.remove(resource);
        }
    }
}
