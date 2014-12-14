package com.jxhobo.http.util;

import android.content.Context;
import com.jxhobo.app.BaseApplication;
import com.jxhobo.http.dataservice.RequestHandler;
import com.jxhobo.http.dataservice.http.*;
import com.jxhobo.http.dataservice.http.impl.BasicHttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA. User: fengerhu Date: 13-5-3 Time: 上午10:35 To
 * change this template use File | Settings | File Templates.
 */
public class HttpUtil {

    private static final String HTTPS_SCHEMA = "https";
    public static final String HTTP_SCHEMA = "http";
    private static final String HOST_NAME = "115.28.141.57";
    private static final String HTTP_PORT = "80";
    private static final String CONTEXT= "game";
    private static final String HTTPS_PORT = "8443";
    private static String CHECK_VERSION_URL = "/appVersion/queryLatest.bin";
    private static String APP_USER_REGISTER_URL = "/appUser/register.bin";
    private static String APP_USER_LOGIN_URL = "/appUser/login.bin";
    private static String APP_USER_LOGOUT_URL = "/appUser/logout.bin";
    private static String APP_USER_QUERY_INFO_URL = "/appUser/queryInfo.bin";
    private static String APP_USER_UPDATE_TOP_SCORE_INFO_URL = "/appUser/updateTopScore.bin";
    private static String APP_GENERATE_INVITE_CODE_URL = "/appUser/generateInviteCode.bin";
    private static String APP_QUERY_LATEST_URL = "/appVersion/queryLatest.bin";
    private static String APP_DOWNLOAD_URL = "/appVersion/download.bin";


    private static ConcurrentHashMap<HttpRequest, Session> runningSession = new ConcurrentHashMap<HttpRequest, Session>();

    private static RequestHandler<HttpRequest, HttpResponse> myHandler = new RequestHandler<HttpRequest, HttpResponse>() {
        @Override
        public void onRequestFailed(HttpRequest request, HttpResponse response) {
            Session session = runningSession.get(request);
            remove(request, session);
            if (session != null && session.handler != null) {
                if (session.status == 2)
                    session.handler.onRequestFailed(request, response);
            }
        }

        @Override
        public void onRequestFinish(HttpRequest request, HttpResponse response) {
            Session session = runningSession.get(request);
            remove(request, session);
            if (session != null && session.handler != null) {
                if (session.status == 2)
                    session.handler.onRequestFinish(request, response);
            }
        }

        @Override
        public void onRequestProgress(HttpRequest request, int current,
                                      int total) {
            Session session = runningSession.get(request);
            if (session != null && session.handler != null) {
                if (session.status == 2)
                    session.handler.onRequestProgress(request, current, total);
            }
        }

        @Override
        public void onRequestStart(HttpRequest request) {
            Session session = runningSession.get(request);
            if (session != null && session.handler != null) {
                if (session.status == 2)
                    session.handler.onRequestStart(request);
            }
        }
    };


    /**
     * 执行http请求
     *
     * @param request
     * @param handler
     */
    public static void httpExec(Context context, HttpRequest request,
                                RequestHandler<HttpRequest, HttpResponse> handler) {
        runningSession.put(request, new Session(request, handler));
        getHttpService(context).exec(request, myHandler);
    }

    /**
     * 执行https请求
     *
     * @param request
     * @param handler
     */
    public static void httpsExec(HttpRequest request,
                                 RequestHandler<HttpRequest, HttpResponse> handler) {
        runningSession.put(request, new Session(request, handler));
        getHttpsService().exec(request, handler);
    }

    /**
     * 创建http请求，请求方式：Get
     *
     * @param action
     * @param map
     * @return
     */
    public static HttpRequest createHttpGetRequest(String action,
                                                   Map<String, Object> map) {
        String actionUrl = getActionUrl(HTTP_SCHEMA, action);
        String paramStr = "";
        if (map != null) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                if (!paramStr.equals(""))
                    paramStr += "&";
                paramStr += key + "=" + map.get(key);
            }
        }
        if (!paramStr.equals(""))
            actionUrl += "?";
        actionUrl += paramStr;
        return BasicHttpRequest.httpGet(actionUrl);
    }


    private static String getActionUrl(String schema, String action) {

        return schema + "://" + HOST_NAME + ":"
                + HTTP_PORT + "/"+CONTEXT+ action;
    }

    /**
     * 创建http请求，请求方式：Post
     *
     * @param action
     * @param data
     * @return
     */
    public static HttpRequest createHttpPostRequest(String action, Object data) {
        return BasicHttpRequest.httpPost(getActionUrl(HTTP_SCHEMA, action), data);
    }

    /**
     * 创建https请求，请求方式：Post
     *
     * @param action
     * @param map
     * @return
     */
    public static HttpRequest createHttpsPostRequest(String action, Map<String, Object> map) {
        return BasicHttpRequest.httpPost(getActionUrl(HTTPS_SCHEMA, action), map);
    }

    /**
     * 获取http服务
     *
     * @return
     */
    public static DefaultHttpService getHttpService(Context context) {
        return (DefaultHttpService) ((BaseApplication) context.getApplicationContext()).instance()
                .getProtocolService(HTTP_SCHEMA);
    }

    /**
     * 获取https服务
     *
     * @return
     */
    public static HttpsService getHttpsService() {
        return (HttpsService) BaseApplication.instance().getProtocolService(
                HTTPS_SCHEMA);
    }

    /**
     * 终止http请求
     *
     * @param request
     */
    public static void abort(Context context, HttpRequest request) {
        Session session = runningSession.get(request);
        if (session != null && session.handler != null) {
            session.status = 0;
            runningSession.remove(request, session.handler);
            getHttpService(context).abort(request, session.handler, true);
        }
    }

    public static void remove(HttpRequest request, Session session) {
        if (request != null) {
            runningSession.remove(request, session);
        }
    }

    /**
     * 检查版本
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest checkVersion(Context context,
                                           RequestHandler<HttpRequest, HttpResponse> handler) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("osType", Constant.OS_TYPE_ANDROID);
        params.put("data", JsonUtil.write2String(data));
        HttpRequest request = createHttpPostRequest(CHECK_VERSION_URL, params);
        httpExec(context, request, handler);
        return request;
    }


    /**
     * 登录
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest login(Context context, String uname, String password,
                                    RequestHandler<HttpRequest, HttpResponse> handler) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("uName", uname);
        data.put("password", password);
        params.put("data", JsonUtil.write2String(data));
        HttpRequest request = createHttpPostRequest(APP_USER_LOGIN_URL, params);
        httpExec(context, request, handler);
        return request;
    }


    /**
     * 登录
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest logout(Context context,
                                     RequestHandler<HttpRequest, HttpResponse> handler) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        params.put("data", "");
        HttpRequest request = createHttpPostRequest(APP_USER_LOGOUT_URL, params);
        httpExec(context, request, handler);
        return request;
    }


    /**
     * 登录
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest queryInfo(Context context,
                                        RequestHandler<HttpRequest, HttpResponse> handler) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        params.put("data", "");
        HttpRequest request = createHttpPostRequest(APP_USER_QUERY_INFO_URL, params);
        httpExec(context, request, handler);
        return request;
    }


    /**
     * 更新最高分
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest updateTopScore(Context context, int topScore,
                                             RequestHandler<HttpRequest, HttpResponse> handler) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("topScore", topScore);
        params.put("data", JsonUtil.write2String(data));
        HttpRequest request = createHttpPostRequest(APP_USER_UPDATE_TOP_SCORE_INFO_URL, params);
        httpExec(context, request, handler);
        return request;
    }

    /**
     * 生成码
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest generateInviteCode(Context context,
                                                 RequestHandler<HttpRequest, HttpResponse> handler) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        params.put("data", "");
        HttpRequest request = createHttpPostRequest(APP_GENERATE_INVITE_CODE_URL, params);
        httpExec(context, request, handler);
        return request;
    }


    /**
     * 检查版本
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest queryLatest(Context context,
                                          RequestHandler<HttpRequest, HttpResponse> handler) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        HashMap<String, Object> data = new HashMap<String, Object>();
        params.put("osType", 1);
        params.put("data", JsonUtil.write2String(data));
        HttpRequest request = createHttpPostRequest(APP_QUERY_LATEST_URL, params);
        httpExec(context, request, handler);
        return request;
    }

    /**
     * 检查版本
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest download(Context context, String md5,
                                       RequestHandler<HttpRequest, HttpResponse> handler) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        HashMap<String, Object> data = new HashMap<String, Object>();
        params.put("md5", md5);
        params.put("data", JsonUtil.write2String(data));
        HttpRequest request = createHttpPostRequest(APP_DOWNLOAD_URL, params);
        httpExec(context, request, handler);
        return request;
    }


    /**
     * 注册
     *
     * @param context
     * @param handler
     * @return
     */
    public static HttpRequest register(Context context, String uname, String password, String code,
                                       RequestHandler<HttpRequest, HttpResponse> handler) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("token", Account.getInstance().getToken());
        params.put("uid", Account.getInstance().getUserId());
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("uName", uname);
        data.put("password", password);
        data.put("inviteCode", code);
        params.put("data", JsonUtil.write2String(data));
        HttpRequest request = createHttpPostRequest(APP_USER_REGISTER_URL, params);
        httpExec(context, request, handler);
        return request;
    }


}
