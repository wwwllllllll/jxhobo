package com.jxhobo.http.dataservice.http.impl;

import com.jxhobo.http.dataservice.http.HttpResponse;
import com.jxhobo.http.dataservice.impl.BasicResponse;
import org.apache.http.NameValuePair;

import java.util.List;

public class BasicHttpResponse extends BasicResponse
        implements HttpResponse {
    private List<NameValuePair> headers;
    private int statusCode;

    public BasicHttpResponse(int statusCode, Object result, List<NameValuePair> headers, Object error) {
        super(result, error);
        this.statusCode = statusCode;
        this.headers = headers;
    }

    public List<NameValuePair> headers() {
        return this.headers;
    }

    public int statusCode() {
        return this.statusCode;
    }
}
