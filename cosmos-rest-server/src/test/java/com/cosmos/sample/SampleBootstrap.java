package com.cosmos.sample;

import com.cosmos.server.commons.exceptions.ControllerRequestMappingException;
import com.cosmos.server.core.context.BeansContext;
import com.cosmos.server.core.http.HttpServerOptions;
import com.cosmos.server.core.http.HttpServerRouteProvider;
import com.cosmos.server.core.http.impl.async.AsyncHttpServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Sample bootstrap.
 */
public class SampleBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(SampleBootstrap.class);

    public static final String VERSION = "1.0.0";

    private ApplicationContext context;

    public SampleBootstrap() {
        long birth = System.currentTimeMillis();
        try {
            logger.info("loading spring context...");

            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                    "classpath*:/spring/applicationContext.xml");
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        context.stop();
                        logger.info("Spring context stopped!");
                    } catch (Throwable e) {
                        logger.error("Spring context stopping failed!", e);
                    }
                }
            });
            context.start();

            this.context = context;
            logger.info("spring context loaded successfully, using {} ms.", (System.currentTimeMillis() - birth));
        } catch (Exception e) {
            logger.error("spring context loaded failed!", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        SampleBootstrap bootstrap = new SampleBootstrap();
        bootstrap.bootRestService();
    }

    private void bootRestService() throws ControllerRequestMappingException {

        logger.info("Starting Sample Server...");
        logger.info("Version: " + VERSION);
        // create http server
        HttpServerRouteProvider server = AsyncHttpServerProvider.create("127.0.0.1", 40001);

        // choose http params, this is unnecessary
        server.useOptions(new HttpServerOptions().setMaxConnections(4096)
                .setHandlerTimeout(10000)
                .setIoThreads(8)
                .setHandlerThreads(256));

        // scan http controller and interceptor
        server.scanHttpController("com.cosmos.sample.web.controller", new BeansContext() {
            @Override
            public <T> T getBean(Class<T> requiredType) {
                try {
                    return context.getBean(requiredType);
                } catch (BeansException e) {
                    return null;
                }
            }
        });

        // start http server
        if (!server.start()) {
            System.err.println("HttpServer run failed");
        }

        logger.info("Sample Server started...");
        try {
            // join and wait here
            server.join();
            server.shutdown();
        } catch (InterruptedException ignored) {
        }

        // would not to reach here as usual ......
    }

}
