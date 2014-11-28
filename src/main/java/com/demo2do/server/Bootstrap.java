package com.demo2do.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务器启动类
 *
 * @author David
 */
public final class Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static final String VERSION = "1.0.0.001";

    private static final String SHUTDOWN_HOOK_KEY = "server.shutdown.hook";

    private static volatile boolean running = true;

    /**
     * 加载整个Server框架的Main函数，完成功能如下：
     *
     * 1. 启动Spring容器
     * 2. 注册shutdown hook
     * 3. 等待Spring容器运行结束后退出
     *
     * 其中，Spring配置路径为：
     *
     * classpath*:/context/applicationContext-*.xml
     *
     * @param args
     */
    public static void main(String[] args) {
        long birth = System.currentTimeMillis();

        try {
            logger.warn("Server Framework init...");
            logger.warn("Version: " + VERSION);

            // 加载Spring容器
            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/context/applicationContext-*.xml");

            logger.warn("user.dir: " + System.getProperty("user.dir"));

            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY))) {
                // 注册钩子线程
                // 在Runtime注册后，如果JVM要停止，shutdown hook便开始执行。
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    /**
                     * 1. 释放Spring容器占用的资源
                     * 2. 置running标志，通知Bootstrap运行结束
                     */
                    public void run() {
                        try {
                            context.stop();
                            logger.warn("Server stopped!");
                        } catch (Throwable t) {
                            logger.error(t.getMessage(), t);
                        }

                        synchronized (Bootstrap.class) {
                            running = false;
                            Bootstrap.class.notify();
                        }
                    }
                });
            }

            // 启动Spring容器
            context.start();
            logger.warn("Server Framework started! takes " + (System.currentTimeMillis() - birth) + " ms");
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            System.exit(1);
        }

        // 等待，直至Spring容器运行结束
        synchronized (Bootstrap.class) {
            while (running) {
                try {
                    Bootstrap.class.wait();
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }
            }
        }
    }
}
