package com.jxhobo.http.dataservice.http;

import android.content.Context;
import android.os.SystemClock;
import com.jxhobo.http.dataservice.RequestHandler;
import com.jxhobo.http.dataservice.http.impl.BasicHttpRequest;
import com.jxhobo.http.dataservice.http.impl.BasicHttpResponse;
import com.jxhobo.http.util.Log;
import com.jxhobo.http.util.MyTask;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class DefaultHttpService implements HttpService {
    private static final String TAG = "http";
    protected Context context;
    protected Executor executor;
    protected final int timeout = 5000;
    protected final ConcurrentLinkedQueue<HttpClient> httpClients = new ConcurrentLinkedQueue<HttpClient>();
    protected NetworkInfoHelper networkInfo;
    protected final ConcurrentHashMap<HttpRequest, Task> runningTasks = new ConcurrentHashMap<HttpRequest, Task>();

    public DefaultHttpService(Context context, Executor executor) {
        this.context = context;
        this.executor = executor;
        this.networkInfo = new NetworkInfoHelper(this.context);
    }

    private HttpClient getHttpClient() {
        HttpClient httpClient = this.httpClients.poll();
        if (httpClient == null) {
            httpClient = createHttpClient();
        }
        return httpClient;
    }

    private void recycleHttpClient(HttpClient client) {
        if (this.httpClients.size() < 4) {
            this.httpClients.add(client);
        } else {
            client.getConnectionManager().shutdown();
        }

    }

    /**
     * 中止访问
     *
     * @param request
     * @param handler
     * @param flag
     */
    public void abort(HttpRequest request,
                      RequestHandler<HttpRequest, HttpResponse> handler, boolean flag) {
        Task task = this.runningTasks.get(request);
        if ((task != null) && (task.handler == handler)) {
            this.runningTasks.remove(request, task);
            task.cancel(flag);
        }
    }

    /**
     * @deprecated
     */
    public void close() {
    }

    protected HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params,timeout);
        HttpClient client = new DefaultHttpClient(params);

        return client;
    }

    protected Task createTask(HttpRequest request,
                              RequestHandler<HttpRequest, HttpResponse> handler) {
        return new Task(request, handler);
    }

    public void exec(HttpRequest request,
                     RequestHandler<HttpRequest, HttpResponse> handler) {
        Task task = createTask(request, handler);
        if (this.runningTasks.putIfAbsent(request, task) != null)
            Log.e("http", "cannot exec duplicate request (same instance)");
        else
            task.executeOnExecutor(this.executor, new Void[0]);
    }


    public HttpResponse execSync(HttpRequest request) {
        return (HttpResponse) createTask(request, null).doInBackground(
                new Object[0]);
    }

    protected boolean isLoggable() {
        return Log.isLoggable(3);
    }

    protected void log(String tag, String log) {
        Log.d(tag, log);
    }

    public int runningTasks() {
        return this.runningTasks.size();
    }

    protected class Task extends MyTask implements WatchedInputStream.Listener {
        protected int availableBytes;
        protected int contentLength;
        protected final RequestHandler<HttpRequest, HttpResponse> handler;
        protected boolean isUploadProgress;
        protected long prevProgressTime;
        protected int receivedBytes;
        protected final HttpRequest req;
        protected HttpUriRequest request;
        protected int sentBytes;
        protected long startTime;
        protected int statusCode;

        public Task(HttpRequest request,
                    RequestHandler<HttpRequest, HttpResponse> handler) {
            super();
            this.req = request;
            this.handler = handler;
        }

        @Override
        public Object doInBackground(Object[] objects) {
            HttpClient httpclient = null;
            BasicHttpResponse basicHttpResponse = null;
            InputStream input = req.input();
            try {
                if (input != null && input.markSupported()) {
                    input.mark(16384);
                }
                request = getUriRequest();
                httpclient = getHttpClient();
                org.apache.http.HttpResponse response = httpclient.execute(request);
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                long length = entity.getContentLength();
                byte[] bytes;
                int k;
                if (length > Integer.MAX_VALUE || length < 0L) {
                    length = -1L;
                }
                contentLength = (int) length;
                receivedBytes = 0;

                if (!BasicHttpRequest.GET.equals(req.method())
                        || contentLength < 4096 || handler == null) {
                    bytes = EntityUtils.toByteArray(entity);
                } else {
                    ByteArrayBuffer buffer = new ByteArrayBuffer(contentLength);
                    byte[] cacheBytes = new byte[4096];
                    long lastTime = 0L;
                    InputStream content = entity.getContent();
                    while ((k = content.read(cacheBytes)) != -1) {
                        buffer.append(cacheBytes, 0, k);

                        receivedBytes = k + receivedBytes;
                        if (receivedBytes < contentLength) {
                            long currentTime = SystemClock.elapsedRealtime();
                            if (currentTime - lastTime > 200L) {
                                publishProgress(new Void[0]);
                                lastTime = currentTime;
                            }
                        } else {
                            publishProgress(new Void[0]);
                        }
                    }
                    bytes = buffer.toByteArray();
                    content.close();
                }

                entity.consumeContent();
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>(8);
                Header[] aheader = response.getAllHeaders();
                for (int j = 0; j < aheader.length; j++) {
                    pairs.add(new BasicNameValuePair(aheader[j].getName(),
                            aheader[j].getValue()));
                }
                basicHttpResponse = new BasicHttpResponse(statusCode, bytes,
                        pairs, null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(e.toString());
                basicHttpResponse = new BasicHttpResponse(0, null, null, e);
            } finally {
                if (httpclient != null) {
                    recycleHttpClient(httpclient);
                }
                if (input != null) {
                    try {
                        input.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return basicHttpResponse;
        }

        private HttpUriRequest getUriRequest() throws Exception {
            HttpUriRequest httpUriRequest;
            if (BasicHttpRequest.GET.equals(req.method())) {
                httpUriRequest = new HttpGet(req.url());
            } else if (BasicHttpRequest.POST.equals(req.method())) {
                httpUriRequest = new HttpPost(req.url());
                InputStreamEntity inp = null;
                if (req.input() != null) {
                    availableBytes = req.input().available();
                    sentBytes = 0;
                    if (availableBytes > 4096) {
                        WatchedInputStream watchedInputStream = new WatchedInputStream(req.input(), 4096);
                        watchedInputStream.setListener(this);
                        isUploadProgress = true;
                        inp = new InputStreamEntity(watchedInputStream, watchedInputStream.available());
                    } else {
                        inp = new InputStreamEntity(req.input(), req.input().available());
                    }

                    ((HttpPost) (httpUriRequest)).setEntity(inp);
                }
            } else if (BasicHttpRequest.PUT.equals(req.method())) {
                httpUriRequest = new HttpPut(req.url());
                if (req.input() != null) {
                    availableBytes = req.input().available();
                    sentBytes = 0;
                    if (availableBytes > 4096) {
                        WatchedInputStream watchedInputStream = new WatchedInputStream(req.input(), 4096);
                        watchedInputStream.setListener(this);
                        isUploadProgress = true;
                        ((HttpPost) (httpUriRequest))
                                .setEntity(new InputStreamEntity(watchedInputStream, watchedInputStream.available()));

                    } else
                        ((HttpPut) (httpUriRequest))
                                .setEntity(new InputStreamEntity(req.input(),
                                        req.input().available()));
                }
            } else if (BasicHttpRequest.DELETE.equals(req.method())) {
                httpUriRequest = new HttpDelete(req.url());
            } else {
                throw new IllegalArgumentException((new StringBuilder())
                        .append("unknown http method ").append(req.method())
                        .toString());
            }

            if (req.headers() != null) {
                Iterator<?> iterator = req.headers().iterator();
                while (iterator.hasNext()) {
                    NameValuePair namevaluepair = (NameValuePair) iterator
                            .next();
                    httpUriRequest.addHeader(namevaluepair.getName(),
                            namevaluepair.getValue());
                }
            }
            org.apache.http.HttpHost httphost = networkInfo.getProxy();
            ConnRouteParams.setDefaultProxy(httpUriRequest.getParams(),
                    httphost);
            return httpUriRequest;
        }

        public void notify(int i) {
            if ((this.handler != null) && (this.isUploadProgress)) {
                this.sentBytes = this.sentBytes + i;
                if (this.sentBytes < this.availableBytes) {
                    long currentTime = SystemClock.elapsedRealtime();
                    if (currentTime - this.prevProgressTime > 200L) {
                        publishProgress(new Void[0]);
                        this.prevProgressTime = currentTime;
                    }
                } else {
                    publishProgress(new Void[0]);
                }
            }
        }

        protected void onCancelled() {
            if (DefaultHttpService.this.isLoggable()) {
                long l = SystemClock.elapsedRealtime() - this.startTime;
                StringBuilder builder = new StringBuilder();
                builder.append("abort (");
                builder.append(this.req.method()).append(',');
                builder.append(this.statusCode).append(',');
                builder.append(l).append("ms");
                builder.append(") ").append(this.req.url());
                DefaultHttpService.this.log(TAG, builder.toString());
                if (this.req.input() instanceof FormInputStream) {
                    FormInputStream localFormInputStream = (FormInputStream) this.req
                            .input();
                    DefaultHttpService.this.log(TAG, "    "
                            + localFormInputStream.toString());
                }
            }
            if (this.request != null)
                this.request.abort();
        }

        @Override
        protected void onPostExecute(Object data) {
            HttpResponse response = (HttpResponse) data;
            if (DefaultHttpService.this.runningTasks.remove(this.req, this)) {
                if (response.result() == null)
                    this.handler.onRequestFailed(this.req, response);
                else
                    this.handler.onRequestFinish(this.req, response);

                if (DefaultHttpService.this.isLoggable()) {
                    long l = SystemClock.elapsedRealtime() - this.startTime;
                    StringBuilder builder = new StringBuilder();
                    if (response.result() == null)
                        builder.append("fail (");
                    else
                        builder.append("finish (");
                    builder.append(this.req.method()).append(',');
                    builder.append(this.statusCode).append(',');
                    builder.append(l).append("ms");
                    builder.append(") ").append(this.req.url());
                    DefaultHttpService.this.log(TAG, builder.toString());
                    if ((this.req.input() instanceof FormInputStream)) {
                        FormInputStream inputStream = (FormInputStream) this.req
                                .input();
                        DefaultHttpService.this.log(TAG,
                                "    " + inputStream.toString());
                    }
                    if (response.result() == null)
                        DefaultHttpService.this.log(TAG,
                                "    " + response.error());
                }
            }
        }

        protected void onPreExecute() {
            handler.onRequestStart(req);
            this.startTime = SystemClock.elapsedRealtime();
        }

        protected void onProgressUpdate(Object[] objects) {
            if (!this.isUploadProgress)
                this.handler.onRequestProgress(this.req, this.receivedBytes,
                        this.contentLength);
            else
                this.handler.onRequestProgress(this.req, this.sentBytes,
                        this.availableBytes);
        }
    }
}