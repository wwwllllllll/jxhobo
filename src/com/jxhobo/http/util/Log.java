package com.jxhobo.http.util;

public class Log {
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static int LEVEL = 0;
    private static String TAG_DEFAULT = "GBCOM_LOG";
    public static final int VERBOSE = 2;
    public static final int WARN = 5;

    public static void d(String paramString) {
        if (3 >= LEVEL)
            android.util.Log.d(TAG_DEFAULT, paramString);
    }

    public static void d(String paramString1, String paramString2) {
        if (3 >= LEVEL)
            android.util.Log.d(paramString1, paramString2);
    }

    public static void d(String paramString1, String paramString2, Throwable paramThrowable) {
        if (3 >= LEVEL)
            android.util.Log.d(paramString1, paramString2, paramThrowable);
    }

    public static void e(String content) {
        if (6 >= LEVEL)
            android.util.Log.e(TAG_DEFAULT, content);
    }

    public static void e(String content, Throwable throwable) {
        if (6 >= LEVEL)
            android.util.Log.e(TAG_DEFAULT, content, throwable);
    }

    public static void e(String title, String content) {
        if (6 >= LEVEL)
            android.util.Log.e(title, content);
    }

    public static void e(String title, String content, Throwable throwable) {
        if (6 >= LEVEL)
            android.util.Log.e(title, content, throwable);
    }

    public static void i(String paramString) {
        if (4 >= LEVEL)
            android.util.Log.i(TAG_DEFAULT, paramString);
    }

    public static void i(String paramString1, String paramString2) {
        if (4 >= LEVEL)
            android.util.Log.i(paramString1, paramString2);
    }

    public static void i(String paramString1, String paramString2, Throwable paramThrowable) {
        if (4 >= LEVEL)
            android.util.Log.i(paramString1, paramString2, paramThrowable);
    }

    public static boolean isLoggable(int paramInt) {
        boolean bool;
        if (paramInt < LEVEL)
            bool = false;
        else
            bool = true;
        return bool;
    }

    public static void v(String paramString) {
        if (2 >= LEVEL)
            android.util.Log.v(TAG_DEFAULT, paramString);
    }

    public static void v(String paramString1, String paramString2) {
        if (2 >= LEVEL)
            android.util.Log.v(paramString1, paramString2);
    }

    public static void v(String paramString1, String paramString2, Throwable paramThrowable) {
        if (2 >= LEVEL)
            android.util.Log.v(paramString1, paramString2, paramThrowable);
    }

    public static void w(String paramString) {
        if (5 >= LEVEL)
            android.util.Log.w(TAG_DEFAULT, paramString);
    }

    public static void w(String paramString1, String paramString2) {
        if (5 >= LEVEL)
            android.util.Log.w(paramString1, paramString2);
    }

    public static void w(String paramString1, String paramString2, Throwable paramThrowable) {
        if (5 >= LEVEL)
            android.util.Log.w(paramString1, paramString2, paramThrowable);
    }
}
