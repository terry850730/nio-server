package io.terrence.nio.server.handler;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public interface MessageHandlerFactory {

    MessageHandler getHandler();

}
