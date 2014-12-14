package com.jxhobo.http.dataservice.http;

import java.io.IOException;
import java.io.InputStream;

public class WatchedInputStream extends InputStream {
    private Listener listener;
    private int notifyBytes;
    private int remains;
    private InputStream stream;

    public WatchedInputStream(InputStream inputStream, int notifyBytes) {
        this.stream = inputStream;
        this.notifyBytes = notifyBytes;
    }

    public int available()
            throws IOException {
        return this.stream.available();
    }

    public void close()
            throws IOException {
        this.stream.close();
    }

    /**
     * @deprecated
     */
    public void mark(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean markSupported() {
        return false;
    }

    public int read()
            throws IOException {
        int i = this.stream.read();
        if (i >= 0) {
            this.remains = 1 + this.remains;
            if (this.remains > this.notifyBytes) {
                if (this.listener != null)
                    this.listener.notify(this.remains);
                this.remains = 0;
            }
        }
        return i;
    }

    public int read(byte[] bytes)
            throws IOException {
        return read(bytes, 0, bytes.length);
    }

    public int read(byte[] bytes, int offset, int length)
            throws IOException {
        int i = this.stream.read(bytes, offset, length);
        if (i >= 0) {
            this.remains = i + this.remains;
            if (this.remains > this.notifyBytes) {
                if (this.listener != null) {
                    this.listener.notify(this.remains);
                    this.remains = 0;
                }
            }
        } else {
            this.listener.notify(this.remains);
        }
        return i;
    }

    /**
     * @deprecated
     */
    public void reset()
            throws IOException {
        throw new IOException("not supported operation: reset");
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public long skip(long l)
            throws IOException {
        throw new IOException("not supported operation: skip");
    }

    public static abstract interface Listener {
        public abstract void notify(int i);
    }
}
