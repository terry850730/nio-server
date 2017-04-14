package io.terrence.nio.server.http;

import io.terrence.nio.server.handler.MessageHandler;
import io.terrence.nio.server.handler.MessageProcessor;
import io.terrence.nio.server.handler.MessageReader;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public class HttpMessageHandler implements MessageHandler<HttpMessage> {

    private HttpMessageReader reader = new HttpMessageReader();
    private HttpMessageProcessor processor = new HttpMessageProcessor();

    @Override
    public MessageReader<HttpMessage> getReader() {
        return reader;
    }

    @Override
    public MessageProcessor getProcessor() {
        return processor;
    }
}
