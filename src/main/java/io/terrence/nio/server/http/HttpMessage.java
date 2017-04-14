package io.terrence.nio.server.http;

import io.terrence.nio.server.message.Message;

import java.nio.ByteBuffer;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public class HttpMessage implements Message {
    private HttpHeader header;

    private byte[] body;

    @Override
    public ByteBuffer getByteBuffer() {
        byte[] headBytes = header.toBytes();
        int length = header.contentLength;
        ByteBuffer byteBuffer = ByteBuffer.allocate(headBytes.length + length);
        byteBuffer.put(headBytes);
        byteBuffer.put(body);
        return byteBuffer;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public void setHeader(HttpHeader header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
