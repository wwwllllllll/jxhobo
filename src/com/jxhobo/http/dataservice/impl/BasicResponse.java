package com.jxhobo.http.dataservice.impl;


import com.jxhobo.http.dataservice.Response;

public class BasicResponse
        implements Response {
    private Object error;
    private Object result;

    public BasicResponse(Object result, Object error) {
        this.result = result;
        this.error = error;
    }

    public Object error() {
        return this.error;
    }

    public Object result() {
        return this.result;
    }
}
