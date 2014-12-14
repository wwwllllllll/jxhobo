package com.jxhobo.http.dataservice;

public abstract interface Response {
    public static final Object SUCCESS = null;

    public abstract Object error();

    public abstract Object result();
}

