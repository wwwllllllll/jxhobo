package com.jxhobo.http.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: fengerhu
 * Date: 14-4-14
 * Time: 下午1:36
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtil {
    /*  */

    public static <T> T read(String jsonData, Class<T> type) {
        try {
            return new ObjectMapper().readValue(jsonData, type);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
            return null;
        }
    }


    public static <T> T read(String jsonData, TypeReference<T> type) {
        try {
            return (T) new ObjectMapper().readValue(jsonData, type);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
            return null;
        }
    }

    public static String write2String(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
            return null;
        }
    }


}
