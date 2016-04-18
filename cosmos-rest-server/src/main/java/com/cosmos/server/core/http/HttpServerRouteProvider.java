package com.cosmos.server.core.http;

import com.cosmos.server.commons.annotations.Interceptor;
import com.cosmos.server.commons.annotations.RequestMapping;
import com.cosmos.server.commons.annotations.Rest;
import com.cosmos.server.commons.constant.http.RequestMethod;
import com.cosmos.server.commons.exceptions.ControllerRequestMappingException;
import com.cosmos.server.commons.utils.ClassUtils;
import com.cosmos.server.commons.utils.PackageScanner;
import com.cosmos.server.core.http.rest.ControllerRouter;
import com.cosmos.server.core.http.rest.URLResource;
import com.cosmos.server.core.http.rest.controller.DefaultController;
import com.cosmos.server.core.http.rest.controller.URLController;
import com.cosmos.server.core.http.rest.interceptor.HttpInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link HttpServerProvider} with {@link ControllerRouter} to dispatch http request.
 *
 * @author BSD
 */
public abstract class HttpServerRouteProvider extends HttpServerProvider {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerRouteProvider.class);

    // controller router
    private static final ControllerRouter routerTable = new ControllerRouter();

    // http interceptors
    private static final List<HttpInterceptor> interceptors = new LinkedList<>();

    // http URI root path
    private static final String ROOT_PATH = "/";

    static {
        // default Controller (URI path is "/")
        Method root = DefaultController.class.getMethods()[0];
        routerTable.register(
                URLResource.fromHttp(ROOT_PATH, RequestMethod.GET),
                URLController.fromProvider(ROOT_PATH, DefaultController.class, root).internal());
    }

    // package scanner
    private PackageScanner scanner = new PackageScanner();

    /**
     * Disable internal controller used for statistics.
     */
    public static void disableInternalController() {
        routerTable.unregister(URLResource.fromHttp(ROOT_PATH, RequestMethod.GET));
    }

    /**
     * Scan specified package for http controller and interceptor.  NOT Threads-Safe !!
     *
     * @param packageName package name
     * @return http server router provider
     * @throws ControllerRequestMappingException
     */
    public HttpServerRouteProvider scanHttpController(String packageName, ApplicationContext context) throws ControllerRequestMappingException {
        // find all Class
        List<Class<?>> classList = null;
        try {
            classList = scanner.scan(packageName);
        } catch (ClassNotFoundException e) {
            logger.error("scan package {} failed!", packageName, e);
            throw new ControllerRequestMappingException(String.format("scan package %s failed. %s", packageName, e.getMessage()));
        }

        RequestMapping clazzLevelRequestMapping = null;

        for (Class<?> clazz : classList) {
            // @Interceptor
            if (clazz.getAnnotation(Interceptor.class) != null) {
                checkConstructor(clazz);
                // must implements HttpInterceptor
                if (clazz.getSuperclass() != HttpInterceptor.class)
                    throw new ControllerRequestMappingException(String.format("%s must implements %s", clazz.getName(), HttpInterceptor.class.getName()));

                try {

                    /**
                     * we register interceptors with class-name natural sequence. may
                     * be known as unordered. But make insurance that call with registered
                     * sequence.
                     *
                     * TODO : may be we can indicate the previous {@link Interceptor} manually
                     *
                     */
                    interceptors.add((HttpInterceptor) clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ControllerRequestMappingException(String.format("%s newInstance() failed %s", clazz.getName(), e.getMessage()));
                }
            }

            // with @Controller
            if (clazz.getAnnotation(Rest.class) != null) {
                checkConstructor(clazz);
                // class level prefix RequestMapping.URL
                clazzLevelRequestMapping = clazz.getAnnotation(RequestMapping.class);

                // find all methods in class
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (Modifier.isStatic(method.getModifiers()) || method.getAnnotations().length == 0)
                        continue;

                    // read @RequestMapping
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    if (requestMapping == null)
                        continue;

                    String uri = requestMapping.value();
                    if (!uri.startsWith("/"))
                        throw new ControllerRequestMappingException(String.format("%s.%s annotation must start with / ", clazz.getName(), method.getName()));

                    if (clazzLevelRequestMapping != null)
                        uri = clazzLevelRequestMapping.value() + uri;


                    // default is RequestMethod.GET if method annotation is not set
                    RequestMethod requestMethod = requestMapping.method();

                    URLResource urlResource = URLResource.fromHttp(uri, requestMethod);
                    Object bean = context.getBean(clazz);
                    URLController urlController = null;
                    if(bean == null) {
                        urlController = URLController.fromProvider(uri, clazz, method);
                    } else {
                        urlController = URLController.fromProvider(uri, clazz, method, bean);
                    }

                    /**
                     * register the controller to controller map {@link ControllerRouter#register(URLResource, URLController)}
                     * will return false on duplicated URLResource key. Duplicated URLResource means they have same url,
                     * url variables and http method. we will confuse on them and couldn't decide which
                     * controller method to invoke.
                     *
                     * TODO : we throw exception here. let users to know and decide what to do
                     *
                     */
                    if (!routerTable.register(urlResource, urlController))
                        throw new ControllerRequestMappingException(String.format("%s.%s annotation is duplicated", clazz.getName(), method.getName()));

                    // add monitor
                    HttpServerStats.resourceMap.put(urlResource, urlController);
                }
            }
        }

        return this;
    }

    /**
     * Check whether a class has a default constructor.
     *
     * @param clazz class
     * @return true if a class has a default constructor, false otherwise
     * @throws ControllerRequestMappingException
     */
    private boolean checkConstructor(Class<?> clazz) throws ControllerRequestMappingException {
        if (!ClassUtils.hasDefaultConstructor(clazz)) {
            throw new ControllerRequestMappingException(String.format("%s doesn't have default constructor", clazz.getName()));
        }
        return true;
    }

    /**
     * Get controller router
     *
     * @return controller router
     */
    public ControllerRouter getRouteController() {
        return routerTable;
    }

    /**
     * Get http interceptors
     *
     * @return http interceptors
     */
    public List<HttpInterceptor> getInterceptor() {
        return interceptors;
    }
}
