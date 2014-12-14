package com.jxhobo.app;

import android.content.Context;
import com.jxhobo.http.dataservice.http.DefaultHttpService;
import com.jxhobo.http.dataservice.http.HttpService;
import com.jxhobo.http.dataservice.http.HttpsService;
import com.jxhobo.http.util.Log;

import java.util.concurrent.*;

class ServiceManager {
    private final Context context;
    private HttpService http;

    public ServiceManager(Context context) {
        this.context = context;
    }

    /**
     * 根据协议类型获取相应的协议服务
     *
     * @param serviceName 协议名称
     * @return service
     */
    public HttpService getService(String serviceName) {
        try {
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
            Executor executor = new ThreadPoolExecutor(2, 6, 60L, TimeUnit.SECONDS, workQueue);
            if ("http".equals(serviceName)) {
                this.http = new DefaultHttpService(this.context, executor);
            } else if ("https".equals(serviceName)) {
                this.http = new HttpsService(this.context, executor);
            } else {
                Log.e("unknown service \"" + serviceName + "\"");
            }
            return this.http;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
