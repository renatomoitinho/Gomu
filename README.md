
gomu
====

Simple web framework experience, java8 + akka + servlet call async

sample use

```java

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

```