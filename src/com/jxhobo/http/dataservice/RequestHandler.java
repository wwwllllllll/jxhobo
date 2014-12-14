package com.jxhobo.http.dataservice;

public abstract interface RequestHandler<T extends Request, R extends Response> {
    public abstract void onRequestFailed(T request, R response);

    public abstract void onRequestFinish(T request, R response);

    public abstract void onRequestProgress(T request, int current, int total);

    public abstract void onRequestStart(T request);
}
