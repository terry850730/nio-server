package io.terrence.nio.server.channel;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by Terrence on 2017/4/12.
 * Desc:
 */
public interface ChannelHandler {

    void init() throws IOException;

    void addChannel(SocketChannel socketChannel);
}
