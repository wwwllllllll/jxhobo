package com.jxhobo.http.dataservice.http;

import com.jxhobo.http.util.JsonUtil;
import com.jxhobo.http.util.WrapInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;

public class FormInputStream extends WrapInputStream {
    public static final String DEFAULT_CHARSET = "UTF-8";
    private String charsetName;
    private Object data;

    public FormInputStream() {
        this(DEFAULT_CHARSET);
    }

    public FormInputStream(String charset) {
        this.charsetName = charset;
    }

    public FormInputStream(Object values) {
        data = values;

    }

    private String encode() throws UnsupportedEncodingException {
        this.charsetName = DEFAULT_CHARSET;
        return JsonUtil.write2String(data);
    }

    public String charsetName() {
        return this.charsetName;
    }

    protected InputStream wrappedInputStream() throws IOException {
        try {
            return new ByteArrayInputStream(encode()
                    .getBytes(this.charsetName));
        } catch (UnsupportedCharsetException e) {
            throw new IOException(e.getMessage());
        }
    }
}
