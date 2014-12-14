package com.jxhobo.http.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: fengerhu
 * Date: 14-11-4
 * Time: 下午5:58
 * To change this template use File | Settings | File Templates.
 */
public class CommonMsg {
    private String data;
    private Integer resultCode;

    public static CommonMsg deSerializeJson(byte[] bytes) {
        String json = null;
        try {
            json = new String(bytes, "UTF-8");
            return JsonUtil.read(json, CommonMsg.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        }
        return null;
    }

    public static CommonMsg deSerializeJson(String json) {
        return JsonUtil.read(json, CommonMsg.class);
    }

    public static HashMap<String, Object> createMap(Long uId, String token, String data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("uId", uId);
        map.put("token", token);
        map.put("data", data);
        return map;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }
}
