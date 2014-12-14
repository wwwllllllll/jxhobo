package com.jxhobo.http.dataservice.http;

import com.jxhobo.http.dataservice.Response;
import org.apache.http.NameValuePair;

import java.util.List;

public abstract interface HttpResponse extends Response {
    public abstract List<NameValuePair> headers();

    public abstract int statusCode();
}

