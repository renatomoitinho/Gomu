package com.app.start.util;

import com.app.start.service.Services;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 09/03/14 17:42
 */
public enum Methods {
    POST,GET,PUT,DELETE;

    static final ConcurrentMap<Methods,Map<String, Services>> mapping =
            new ConcurrentLinkedHashMap.Builder<Methods,Map<String, Services>>()
            .maximumWeightedCapacity(100)
            .build();

    static {
        for (Methods methods: Methods.values()){
            mapping.put(methods, new HashMap<>());
        }
    }

    public static Methods self(String str){
        return Methods.valueOf(str.toUpperCase());
    }

    public void put(String originalPath, Services rockService){
        mapping.get(this).put(originalPath,rockService);
    }

    public Map<String, Services> mapped(){
       return mapping.get(this);
    }

}
