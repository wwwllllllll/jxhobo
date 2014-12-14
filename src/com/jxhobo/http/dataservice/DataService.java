package com.jxhobo.http.dataservice;

public abstract interface DataService<T extends Request, R extends Response> {
    public abstract void abort(T request, RequestHandler<T, R> handler, boolean flag);

    public abstract void exec(T request, RequestHandler<T, R> handler);

    public abstract R execSync(T request);
}
