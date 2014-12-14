package com.jxhobo.http.dataservice.http;

import com.jxhobo.http.dataservice.Request;
import org.apache.http.NameValuePair;

import java.io.InputStream;
import java.util.List;

public abstract interface HttpRequest extends Request {
    public abstract List<NameValuePair> headers();

    public abstract InputStream input();

    public abstract String method();
}
