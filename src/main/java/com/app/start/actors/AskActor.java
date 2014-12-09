package com.app.start.actors;

import akka.actor.UntypedActor;
import com.app.start.service.CommonService;
import com.app.start.service.ResultService;
import com.app.start.util.Result;
import org.apache.log4j.Logger;
import scala.concurrent.duration.Duration;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 17/11/14 17:52
 */
public class AskActor extends UntypedActor {



    final private AsyncContext asyncContext;
    final static AtomicInteger count = new AtomicInteger(0);
    final Logger log = Logger.getLogger(AskActor.class);


    public AskActor(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    //    ActorRef testActor = getContext().actorOf(Props.create(new TestActor.TestActorCreator()), "TestActor");
    //   getContext().watch(getSelf());
    //   getContext().setReceiveTimeout(Duration.create("5 seconds"));
    //    testActor.tell("Test!!!", getSelf());
    }


    private void call(CommonService service,HttpServletRequest servletRequest,HttpServletResponse servletResponse){
          service.execute(servletRequest,servletResponse);
    }

    private void call(ResultService service,HttpServletRequest servletRequest,HttpServletResponse servletResponse){
         service.execute(Result.of(servletRequest,servletResponse));
    }

    @Override
    public void onReceive(Object msg) {

        HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
        HttpServletRequest request  = (HttpServletRequest) asyncContext.getRequest();

        try{
            if(msg instanceof ResultService){
                call(((ResultService)msg),request,response);

            }else if(msg instanceof CommonService){
                call(((CommonService)msg),request,response);

            }else
                unhandled(msg);

        }finally {
            asyncContext.complete();
        }
        /*
        try{
        if (msg instanceof ReceiveTimeout) {
            getContext().stop(getSelf());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Timeout");
            asyncContext.complete();
        } else if (msg instanceof Terminated) {
            getContext().stop(getSelf());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unexpectedly Stopped");
            asyncContext.complete();
        } else if (msg instanceof String) {
            getContext().stop(getSelf());
            resp.setContentType("text/plain");
            try (PrintWriter writer = resp.getWriter()) {
                writer.print("on received Ok "+ count.incrementAndGet() );

                log.info( "received ok =>" + count.get() );
            }
            asyncContext.complete();
        } else {
            unhandled(msg);
        }
        }catch (Exception ignore){}
          */
    }


}


