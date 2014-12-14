package com.jxhobo.http.dataservice.http.impl;

import com.jxhobo.http.dataservice.http.FormInputStream;
import com.jxhobo.http.dataservice.http.HttpRequest;
import com.jxhobo.http.dataservice.impl.BasicRequest;
import org.apache.http.NameValuePair;

import java.io.InputStream;
import java.util.List;

public class BasicHttpRequest extends BasicRequest
        implements HttpRequest {
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    private List<NameValuePair> headers;
    private InputStream input;
    private String method;

    public BasicHttpRequest(String url, String method, InputStream inputStream) {
        super(url);
        this.method = method;
        this.input = inputStream;
    }

    public static HttpRequest httpGet(String url) {
        return new BasicHttpRequest(url, GET, null);
    }

    public static HttpRequest httpPost(String url, Object data) {
        FormInputStream formIns = new FormInputStream(data);
        return new BasicHttpRequest(url, POST, formIns);
    }

    public List<NameValuePair> headers() {
        return this.headers;
    }

    public InputStream input() {
        return this.input;
    }

    public String method() {
        return this.method;
    }

    public String toString() {
        return this.method + ": " + super.toString();
    }
}
