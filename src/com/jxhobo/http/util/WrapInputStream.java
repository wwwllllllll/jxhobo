package com.jxhobo.http.util;

import java.io.IOException;
import java.io.InputStream;

public abstract class WrapInputStream extends InputStream {
    private IOException ex;
    private InputStream ins;
    private int marks;


    private InputStream inputStream() throws IOException {
        if (this.ins == null) {
            try {
                this.ins = wrappedInputStream();
            } catch (IOException e) {
                throw e;
            }
        }
        return this.ins;

    }

    public int available()
            throws IOException {
        return inputStream().available();
    }

    public void close()
            throws IOException {
        if (this.ins != null)
            inputStream().close();
    }

    public void mark(int paramInt) {
        if (this.ins != null) {
            this.ins.mark(paramInt);
            this.marks = (1 + this.marks);
        }
    }

    public boolean markSupported() {
        boolean bool = false;
        if (this.ins != null)
            bool = this.ins.markSupported();
        return bool;
    }

    public int read() throws IOException {
        return inputStream().read();
    }

    public int read(byte[] bytes)
            throws IOException {
        return inputStream().read(bytes);
    }

    public int read(byte[] bytes, int offset, int length)
            throws IOException {
        return inputStream().read(bytes, offset, length);
    }

    public void reset() throws IOException {
        if (this.marks == 0)
            this.ins = null;
        else {
            inputStream().reset();
            this.marks = (-1 + this.marks);
        }
    }

    public long skip(long l)
            throws IOException {
        return inputStream().skip(l);
    }

    protected abstract InputStream wrappedInputStream()
            throws IOException;
}
