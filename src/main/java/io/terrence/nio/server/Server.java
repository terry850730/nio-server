package io.terrence.nio.server;

import io.terrence.nio.server.channel.ChannelHandler;
import io.terrence.nio.server.channel.DefaultChannelHandler;
import io.terrence.nio.server.handler.MessageHandlerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by Terrence on 2017/4/12.
 * Desc:
 */
public class Server {

    private static final int DEFAULT_QUEUE_SIZE = 1000;

    private int port;
    private ChannelHandler channelHandler;

    public Server(int port, MessageHandlerFactory factory) throws IOException {
        this(port, factory, DEFAULT_QUEUE_SIZE);
    }

    public Server(int port, MessageHandlerFactory factory, int queueSize) throws IOException {
        this.port = port;
        channelHandler = new DefaultChannelHandler(queueSize, factory);
        channelHandler.init();
    }

    public void start() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));

            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                channelHandler.addChannel(socketChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
