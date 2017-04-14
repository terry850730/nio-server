package io.terrence.nio.server.handler;

/**
 * Created by Terrence on 2017/4/12.
 * Desc:
 */
public interface MessageHandler<T> {

    MessageReader<T> getReader();

    MessageProcessor getProcessor();
}
