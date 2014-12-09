package com.app.start.filters;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.app.start.actors.AskActor;
import com.app.start.util.Methods;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 17/11/14 10:26
 */
@WebFilter(urlPatterns = "/*" , asyncSupported = true)
public class JettyFilter implements Filter {

    Logger log = Logger.getLogger(JettyFilter.class);
    ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        filterConfig.getServletContext().setAttribute("ActorSystem", ActorSystem.create("MySystem"));

        servletContext = filterConfig.getServletContext();
        log.info("init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException(
                    "robocop aren't supported.");
        }

        final Methods methods = Methods.GET;
        final AsyncContext asyncContext = request.startAsync();
        final ActorSystem system = (ActorSystem) request.getServletContext()
                .getAttribute("ActorSystem");

        ActorRef actorRef = system.actorOf(Props.create(AskActor.class, asyncContext));

        actorRef.tell( methods.mapped().get("/hello/json"), actorRef);

    //    log.info( baseRequest.getRequestURI() );

       // JettyListener.newWork(request.startAsync());

        /*
        if (isRequestingStaticFile(baseRequest)){
            deferProcessingToContainer(filterChain, baseRequest, baseResponse);
        } else{


            log.info( baseRequest.getRequestURI() );

           // RobotWork.newWork(request.startAsync());

            /*

            Methods methods = Methods.self(baseRequest.getMethod());

            if(methods!=null)
            {
                String[] keys = methods.mapped().keySet().toArray(new String[ methods.mapped().size()]);

                for(int i =0; i < keys.length;i++){
                    ParamCtrl paramCtrl = DefParamCtrl.of(keys[i]);
                    if(paramCtrl.matches(baseRequest.getRequestURI())){

                        log.info("found url :)" + baseRequest.getRequestURI() );

                        Result result = new Result(baseRequest, baseResponse);
                        result.triplet = methods.mapped().get(keys[i]);

                        paramCtrl.fillIntoRequest(baseRequest.getRequestURI(), result.parameters);
                        request.setAttribute("result", result);
                        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
                        RobotWork.newWork(request.startAsync());
                        return;
                    }
                }
            }

        }
           */


        // filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        log.info("destroy");

    }

    public boolean isRequestingStaticFile(HttpServletRequest request) throws MalformedURLException {
        URL resourceUrl = servletContext.getResource(uriRelativeToContextRoot(request));
        return resourceUrl != null && isAFile(resourceUrl);
    }

    private String uriRelativeToContextRoot(HttpServletRequest request) {
        String uri = request.getRequestURI().substring(request.getContextPath().length());
        return removeQueryStringAndJSessionId(uri);
    }

    private String removeQueryStringAndJSessionId(String uri) {
        return uri.replaceAll("[\\?;].+", "");
    }

    private boolean isAFile(URL resourceUrl) {
        return !resourceUrl.toString().endsWith("/");
    }

    public void deferProcessingToContainer(FilterChain filterChain, HttpServletRequest request,
                                           HttpServletResponse response) throws IOException, ServletException {
        log.info (MessageFormat.format("Deferring request to container: {0} ", request.getRequestURI()));
        filterChain.doFilter(request, response);
    }

}
