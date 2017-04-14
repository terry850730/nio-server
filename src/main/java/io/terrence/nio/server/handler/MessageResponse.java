package io.terrence.nio.server.handler;

import io.terrence.nio.server.message.Message;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public interface MessageResponse {

    void write(Message message);
}
