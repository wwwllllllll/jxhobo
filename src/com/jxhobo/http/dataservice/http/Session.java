package com.jxhobo.http.dataservice.http;

import com.jxhobo.http.dataservice.RequestHandler;

public class Session {
    public RequestHandler<HttpRequest, HttpResponse> handler;
    public HttpRequest request;
    public int status = 2;   //0 取消 , 2 访问http

    public Session() {

    }

    public Session(HttpRequest apiRequest, RequestHandler<HttpRequest, HttpResponse> requestHandler) {
        this.request = apiRequest;
        this.handler = requestHandler;
    }
}