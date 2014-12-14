package com.jxhobo.http.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: fengerhu
 * Date: 14-11-29
 * Time: 下午5:14
 * To change this template use File | Settings | File Templates.
 */
public class ResultCode {

    public static boolean isSuccess(int code) {
        return code == 0;
    }

    public static boolean dealError(Context context, int code) {
        if (!isSuccess(code)) {
            String msg = "";
            switch (code) {
                case 1:
                    msg = "邀请码错误";
                    break;
                case 2:
                    msg = "用户名已经被注册";
                    break;
                case 3:
                    msg = "用户名密码不匹配";
                    break;
                case 4:
                    msg = "非法操作";
                    break;
                case 101:
                    msg = "用户Token失效";
                    break;
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
