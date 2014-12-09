package com.app.start.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renato.moitinho@gmail.com
 * @since 18/12/13 16:34
 */



interface IWrapper{
    DateFormat getDataFormat();
    String getCharset();
}

enum Wrapper{

    String(java.lang.String.class){
        @Override
        JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper) {
            return new JsonPrimitive( (java.lang.String) object);
        }
    },
    Number(java.lang.Number.class){
        @Override
        boolean check(Class clazz) {
            return this.c.isAssignableFrom(clazz);
        }
        @Override
        JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper) {
            return new JsonPrimitive( (java.lang.Number) object);
        }
    },
    Timestamp(java.sql.Timestamp.class){
        @Override
        JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper) {
            return new JsonPrimitive( wrapper.getDataFormat().format((java.sql.Timestamp) object));
        }
    },
    Date(java.util.Date.class){
        @Override
        JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper) {
            return new JsonPrimitive( wrapper.getDataFormat().format((java.util.Date) object));
        }
    },
    Character(java.lang.Character.class){
        @Override
        JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper) {
            return new JsonPrimitive(  (char)object );
        }
    },
    Boolean(java.lang.Boolean.class){
        @Override
        JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper) {
            return new JsonPrimitive(  (boolean)object );
        }
    },
    ByteArray(byte[].class){
        @Override
        JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper) {
            if(wrapper.getCharset()!=null){
                try {
                    return new JsonPrimitive( new String( ((byte[])object) , wrapper.getCharset() ) );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            return new JsonPrimitive( new String( (byte[])object  ));
        }
    }
   ;
    protected Class<?> c;

    private Wrapper(Class<?> aClass){
        this.c = aClass;
    }

    public Class<?> getC() {
        return c;
    }

    boolean check(Class c){
        return c == this.c;
    }

    abstract JsonPrimitive jsonPrimitive(Object object,IWrapper wrapper);

    static Map<Class<?>, Wrapper> map = new HashMap<>();

    static {
        for(Wrapper w : Wrapper.values()){
            map.put(w.getC() , w);
        }
    }

    static Wrapper get(Class clazz) {
        if(java.lang.Number.class.isAssignableFrom(clazz))
            return Number;

        return map.get(clazz);
    }

    static boolean isPrimitive(Class o){
        return o.isPrimitive() || o == java.lang.String.class
                || o== java.lang.Boolean.class
                || o ==java.util.Date.class
                || o ==java.sql.Timestamp.class
                || o == byte[].class
                || java.lang.Number.class.isAssignableFrom(o);
    }


}

final public class JSON {

    JsonObject object = new JsonObject();

    private JSON(JsonBuilder builder){
          object = builder.object;
    }

    public static JsonBuilder  $(){
        return new JsonBuilder();
    }

    public static JSONSerialization.JSONSerializationBuilder Serializable(){
        return  JSONSerialization.$();
    }

    public String toStrArray(){
        return "["+ object.toString() +"]";
    }

    @Override
    public String toString() {
        return object.toString();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD) @Documented
    public @interface Exclude {}

    public final static class JsonBuilder {

        private JsonObject object = new JsonObject();

        public JsonBuilder field(String field,Number value){
           object.addProperty(field, value);
           return this;
        }

        public JsonBuilder field(String field, JSON json){
            object.add(field, json.object);
            return this;
        }

        public JsonBuilder field(String field, JSONArray array){
            object.add(field, array.array);
            return this;
        }

        public JsonBuilder field(String field,Boolean value){
            object.addProperty(field, value);
            return this;
        }


        public JsonBuilder field(String field,String value){
            object.addProperty(field, value);
            return this;
        }
        public JsonBuilder field(String field,Character value){
            object.addProperty(field, value);
            return this;
        }

        public JsonBuilder field(String field, JsonPrimitive primitive){
              object.add(field, primitive);
            return this;
        }


        public JSON build(){
            return new JSON(this);
        }
    }

    //Array
    public final static class JSONArray{

        private JsonArray array = new JsonArray();

        private JSONArray(JSONArrayBuilder jsonArrayBuilder){
             array = jsonArrayBuilder.array;
        }

        public static JSONArrayBuilder $(){
            return new JSONArrayBuilder();
        }

        public String toString(){
            return array.toString();
        }

        public final static class JSONArrayBuilder{

           JsonArray array = new JsonArray();

           public JSONArray build(){
               return  new JSONArray(this);
           }

           public JSONArrayBuilder addAll(JSONArray jsonArray){
               array.addAll(jsonArray.array);
               return this;
           }

           public JSONArrayBuilder add(JsonPrimitive primitive){
               array.add(primitive);
               return this;
           }

           public JSONArrayBuilder add(JSON json){
              array.add(json.object);
               return this;
           }

        }


    }

   //serialization
    public final static class JSONSerialization{

        String[] fields = new String[0];
        String patternField =  ("\\W%s\\W+");
        boolean useAnnotations = true;
        IWrapper wrapper;

        public static JSONSerializationBuilder $(){
            return new JSONSerializationBuilder();
        }

        private JSONSerialization(JSONSerializationBuilder builder , IWrapper wrapper){
            this.fields = builder.fields;
            this.wrapper = wrapper;
            this.useAnnotations = builder.useAnnotations;
        }


       public List<Field> joinASParent(Object obj){

           List<Field> joinFields = new ArrayList<>();

           Class<?> superClass;
           if((superClass = obj.getClass().getSuperclass())!=null){
               joinFields.addAll(Arrays.asList(superClass.getDeclaredFields()));
           }

           joinFields.addAll(joinFields.size(), Arrays.asList(obj.getClass().getDeclaredFields()));

           return  joinFields;
       }


        public JSON toJSON(Object obj) throws IllegalAccessException {

            JsonBuilder builder =  JSON.$();
            String filter = Arrays.toString(fields);

            List<Field> joinFields = joinASParent(obj);

            for(Field f: joinFields){

                if(Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers()) || (useAnnotations && f.isAnnotationPresent(Exclude.class)) )
                    continue;
                if(Pattern.compile(String.format(patternField,f.getName())).matcher(filter).find())
                    continue;

                f.setAccessible(true);
                Object value = f.get(obj);

                if(value==null)
                    continue;

                Class cValue = value.getClass();

                if(Wrapper.isPrimitive(cValue)){
                    // value
                    builder.field(f.getName(), Wrapper.get(cValue).jsonPrimitive(value, wrapper));
                }else if (cValue.isEnum()){
                    builder.field(f.getName() , String.valueOf(value));
                }
                else if(Collection.class.isAssignableFrom(cValue)){
                    builder.field( f.getName(), toJson( (Collection) value) );
                }else if(cValue.isArray()){
                    builder.field(f.getName() , toJson((Object[]) value));
                }
                else {
                    builder.field(f.getName() , toJSON(value));
                }

            }

         return builder.build();
        }

       public JSONArray toJson(Collection collection) throws IllegalAccessException {
              return toJson( (collection).toArray() ) ;
       }


        public JSONArray toJson(Object[] collection) throws IllegalAccessException {
            JSONArray.JSONArrayBuilder arrayBuilder = JSONArray.$();
            for(Object o : collection){

                Class cValue = o.getClass();

                if(Wrapper.isPrimitive(cValue)){
                    arrayBuilder.add(Wrapper.get(cValue).jsonPrimitive(o,wrapper)) ;
                } else {
                    arrayBuilder.add(toJSON(o));
                }

            }

            return arrayBuilder.build();
        }


        public static class JSONSerializationBuilder{

            String[] fields = new String[0];
            String charset;
            String dateFormat = "dd/MM/yyyy HH:mm:ss";
            boolean useAnnotations = true;

            public JSONSerializationBuilder useAnnotation(boolean useAnnotations){
                this.useAnnotations = useAnnotations;
                return this;
            }

            public JSONSerializationBuilder exclude(String... fields){
                this.fields = fields;
                return this;
            }

            public JSONSerializationBuilder setCharset(String charset){
                this.charset = charset;
                return this;
            }

            public JSONSerializationBuilder setDateFormat(String pattern){
                this.dateFormat = pattern;
                return this;
            }

            public String serialize(Object serializable){
                return  build().serialize(serializable);
            }

            public JSONSerialization build(){

                IWrapper wrapper = new IWrapper() {
                    @Override
                    public DateFormat getDataFormat() {
                        return new SimpleDateFormat(dateFormat);
                    }

                    @Override
                    public String getCharset() {
                        return charset;
                    }
                };

                return new JSONSerialization(this, wrapper);
            }
        }


        public String serialize(Object serializable){

           // System.out.println("############ prepare to serializable ####" + serializable.getClass() );

            try {

                if(serializable==null){
                    return "[]";
                }

                if(serializable.getClass().isArray()){
                    return  toJson((Object[]) serializable).toString();
                }

                if(Collection.class.isAssignableFrom(serializable.getClass())){
                    return toJson((Collection)serializable).toString();
                }


                return toJSON(serializable).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return "";
        }


    }


}
