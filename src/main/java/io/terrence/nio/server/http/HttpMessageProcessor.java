package io.terrence.nio.server.http;

import io.terrence.nio.server.handler.MessageProcessor;
import io.terrence.nio.server.handler.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static io.terrence.nio.server.http.HttpUtil.UTF_8;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public class HttpMessageProcessor implements MessageProcessor<HttpMessage> {

    private static Logger logger = LoggerFactory.getLogger(HttpMessageProcessor.class);

    String body = "<html><body>Hello World! <br>Current Time : {date}</body></html>";

    @Override
    public void onMessage(HttpMessage request, MessageResponse response) {
        writeHttpMessage(response, body.replace("{date}", new Date().toString()));
    }

    @Override
    public void onError(Throwable t, MessageResponse response) {
        writeHttpMessage(response, t.getMessage());
    }

    private void writeHttpMessage(MessageResponse response, String body) {
        HttpMessage message = new HttpMessage();
        try {
            // TODO 设置HTTP头
            HttpHeader header = new HttpHeader();
            message.setHeader(header);

            byte[] httpResponseBytes = body.getBytes(UTF_8);
            header.contentLength = httpResponseBytes.length;
            message.setBody(httpResponseBytes);
            logger.info("Send message back.");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        response.write(message);
    }
}
