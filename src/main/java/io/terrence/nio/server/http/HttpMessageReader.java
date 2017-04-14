package io.terrence.nio.server.http;

import io.terrence.nio.server.handler.MessageReader;

import java.nio.ByteBuffer;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public class HttpMessageReader implements MessageReader {

    /**
     * 如果消息分多次发送，则需要缓存部分消息，并通知处理线程，暂缓处理
     *
     * @param byteBuffer    接收到的字节流
     * @return  解析后的消息
     */
    @Override
    public HttpMessage read(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        int remaining = byteBuffer.remaining();
        byte[] bytes = new byte[remaining];
        byteBuffer.get(bytes);
        HttpMessage message = HttpUtil.parseHttpRequest(bytes, 0, remaining);
        return message;
    }
}
