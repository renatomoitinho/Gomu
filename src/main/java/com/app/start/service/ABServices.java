package com.app.start.service;

import com.app.start.util.Methods;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 17/11/14 00:52
 */
public abstract class ABServices {

    public static Services get(String path,CommonService service){
        set(path,service);

        return service;
    }

    public static Services get(String path,ResultService service){
        set(path,service);

        return service;
    }

    private static void set(String path,Services service){
        Methods.self(Thread.currentThread().getStackTrace()[2].getMethodName()).put(path,service);
    }

    public static Rules rules(Class<?> c){
     return null;
    }

    public static Rules rules(Class<?> c, Services services){
        return null;
    }


    public static interface Rules{
        void at(Services services);
    }


}
