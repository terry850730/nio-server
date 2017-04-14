package io.terrence.nio.server.channel;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Terrence on 2017/4/12.
 * Desc:
 */
public class ChannelQueue extends ArrayBlockingQueue<SocketChannel> {

    public ChannelQueue(int capacity) {
        super(capacity);
    }

    public ChannelQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }
}
