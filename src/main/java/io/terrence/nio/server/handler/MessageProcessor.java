package io.terrence.nio.server.handler;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public interface MessageProcessor<T> {

    void onMessage(T request, MessageResponse response);

    void onError(Throwable t, MessageResponse response);
}
