package com.app.start;

import com.app.start.filters.JettyFilter;
import com.app.start.listeners.JettyListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 17/11/14 10:19
 */
public class Application {

    public static void main(String[] args) throws Exception{

        new ApplicationRoutes();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addEventListener(new JettyListener());

        Server server = new Server(9000);

        // The ServletHandler is a dead simple way to create a context handler
        // that is backed by an instance of a Servlet.
        // This handler then needs to be registered with the Server object.

        // Passing in the class for the Servlet allows jetty to instantiate an
        // instance of that Servlet and mount it on a given context path.

        // IMPORTANT:
        // This is a raw Servlet, not a Servlet that has been configured
        // through a web.xml @WebServlet annotation, or anything similar.
        FilterHolder filterHolder = new FilterHolder();
        filterHolder.setName("jetty-filter");
        filterHolder.setFilter(new JettyFilter());
        filterHolder.setAsyncSupported(true);


        context.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST,DispatcherType.ASYNC));

        server.setHandler( context );
        // Start things up!
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                context.getServer().stop();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        },"jetty-stop-server"));

        // The use of server.join() the will make the current thread join and
        // wait until the server is done executing.
        // See
        // http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
        server.join();
    }
}
