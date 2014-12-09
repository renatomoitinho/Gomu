package com.app.start;

import com.app.start.actors.TestActor;
import com.app.start.service.ABServices;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 18/11/14 01:04
 */
public class ApplicationRoutes extends ABServices{

   static {

        get("/hello", (request,response)->{

            try(PrintWriter printWriter= response.getWriter()){
                printWriter.print("ApplicationRoutes hello ;) ");
            } catch (IOException ignored){}

        });

        get("/hello/json", (result)-> result.status(404,"<h1>page not found </h1>"));

        rules(TestActor.class).at(get("/authorized", (result) -> result.ok("it's fine")));

    }
}
