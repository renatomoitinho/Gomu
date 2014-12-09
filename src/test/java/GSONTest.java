import com.app.start.util.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import scala.util.parsing.json.JSON$;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 18/11/14 02:41
 */
public class GSONTest {

    static class User{
        public String name;
        public int age;

        User(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static void main(String[] args){

      Gson gson = new Gson();

        List<User> users = new ArrayList<>();
        users.add(new User("A", 10));
        users.add(new User("B", 20));

        System.out.println( gson.toJson( users));



    }
}
