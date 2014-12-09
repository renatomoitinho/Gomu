package com.app.start.listeners;

import org.apache.log4j.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 17/11/14 11:02
 */
public class JettyListener implements ServletContextListener {

    final static Logger log = Logger.getLogger(JettyListener.class);

    private static final BlockingQueue<AsyncContext> process = new LinkedBlockingQueue<>();
    private static final ExecutorService singleExecute = Executors.newFixedThreadPool(100);


    public static void newWork(AsyncContext c) {
        try {
            process.put(c);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void process(final AsyncContext asyncContext){
        singleExecute.submit((Runnable) () -> {
            try {

                final HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
                final HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

                response.getWriter().print("Run async work.");
                response.getWriter().flush();
            } catch (IOException ignore) {
            } finally {
                asyncContext.complete();
            }
        });

    }


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("contextInitialized");

        singleExecute.submit((Runnable) ()->{

            while (true) {
                try {
                    process( process.take() );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.info("contextDestroyed");
        singleExecute.shutdownNow();
    }


}
