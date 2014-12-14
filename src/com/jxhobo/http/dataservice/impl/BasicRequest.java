package com.jxhobo.http.dataservice.impl;

import com.jxhobo.http.dataservice.Request;

public class BasicRequest
        implements Request {
    private String url;

    public BasicRequest(String url) {
        this.url = url;
    }

    public String toString() {
        return this.url;
    }

    public String url() {
        return this.url;
    }
}
