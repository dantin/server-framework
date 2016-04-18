package com.cosmos.server.core.http.rest;

import com.cosmos.server.commons.constant.http.RequestMethod;
import com.cosmos.server.core.http.utils.HttpUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent URI resource, include http uri and http method.
 *
 * @author BSD
 */
public class URLResource {

    // variable flag
    public static final char VARIABLE = '{';

    // resource identifier
    private URLResourceIdentifier identifier;

    private URLResource() {
        // do nothing here
    }

    /**
     * Creates a {@link URLResource} by input parameter.
     *
     * @param url           url string
     * @param requestMethod method that handler the url
     * @return analyzed url resource
     */
    public static URLResource fromHttp(String url, RequestMethod requestMethod) {
        URLResource resource = new URLResource();
        //
        resource.identifier = URLResourceIdentifier.analyze(url);
        resource.identifier.requestMethod = requestMethod;
        return resource;
    }

    /**
     * Get request method of a {@link URLResource}.
     *
     * @return request method
     */
    public RequestMethod requestMethod() {
        return identifier.requestMethod;
    }

    /**
     * Get url fragments of a {@link URLResource}.
     * @return
     */
    public List<String> fragments() {
        return identifier.fragments;
    }


    @Override
    public String toString() {
        return identifier.toString();
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return !(other == null || !(other instanceof URLResource)) && identifier.equals(((URLResource) other).identifier);
    }

    /**
     * URLResource Unique Identifier
     * <p>
     * Used for URLResource equal judgement and hashcode id
     *
     * @author BSD
     */
    static class URLResourceIdentifier {
        protected List<String> fragments = new ArrayList<>(64);
        protected RequestMethod requestMethod;

        /**
         * Default Constructor
         */
        URLResourceIdentifier() {
            // do nothing here
        }

        /**
         * Factory method to create {@link URLResourceIdentifier} from url string.
         *
         * @param url url string
         * @return URLResourceIdentifier
         */
        public static URLResourceIdentifier analyze(String url) {
            url = HttpUtils.truncateUrl(url);
            URLResourceIdentifier identifier = new URLResourceIdentifier();
            for (String s : Splitter.on('/').trimResults().omitEmptyStrings().split(url)) {
                identifier.fragments.add(s);
            }
            return identifier;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || !(object instanceof URLResourceIdentifier))
                return false;
            URLResourceIdentifier other = (URLResourceIdentifier) object;
            if (requestMethod != other.requestMethod)
                return false;
            // rest array is not necessary
            if (fragments.size() != other.fragments.size())
                return false;
            for (int i = 0; i != fragments.size(); i++) {
                if (!(fragments.get(i).charAt(0) == VARIABLE || other.fragments.get(i).charAt(0) == VARIABLE || fragments.get(i).equals(other.fragments.get(i))))
                    return false;
            }

            return true;
        }

        /**
         * TODO: more URL fields to hash. inefficient hash searching!
         * <p>
         * we calculate hash code without url rest terms array content,
         * it will same as even rest array is different. this can cause hashcode
         * is equaled when http method and url terms array size are same.
         * <p>
         * HashMap (K, V) use hashcode() to determine which bucket is
         * used to hold the Entry. but get() use hashcode() and equals() to search
         * the Entry. so the correctness has no affected but search performance
         * on non-exist Entry searching.
         */
        @Override
        public int hashCode() {
            return requestMethod.name().hashCode() + fragments.size();
        }

        @Override
        public String toString() {
            return String.format("%s, %s", requestMethod.name(), Joiner.on("/").skipNulls().join(fragments));
        }
    }
}
