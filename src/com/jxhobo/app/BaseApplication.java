package com.jxhobo.app;

import android.app.Application;
import android.content.SharedPreferences;
import com.jxhobo.http.dataservice.http.HttpService;
import com.jxhobo.http.util.Constant;

/**
 * Created with IntelliJ IDEA.
 * User: fengerhu
 * Date: 14-11-26
 * Time: 上午9:48
 * To change this template use File | Settings | File Templates.
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;
    private ServiceManager services;


    public BaseApplication() {
        instance = this;
    }

    static BaseApplication _instance() {
        return instance;
    }

    public static BaseApplication instance() {
        if (instance != null)
            return instance;
        throw new IllegalStateException("Application has not been created");
    }

    public void onCreate() {
        super.onCreate();
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Constant.SHARE_PER_NAME, 3);
    }

    /**
     * 根据协议类型获取相应的协议服务
     *
     * @param serviceName
     * @return
     */
    public HttpService getProtocolService(String serviceName) {
        if (this.services == null)
            this.services = new ServiceManager(this);
        return this.services.getService(serviceName);
    }

}
