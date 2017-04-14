package io.terrence.nio.server.channel;

import io.terrence.nio.server.handler.MessageHandler;
import io.terrence.nio.server.message.Message;

import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public class NioChannel {
    private SocketChannel socketChannel;
    private MessageHandler handler;
    private LinkedBlockingQueue messages = new LinkedBlockingQueue(100);

    public NioChannel(SocketChannel socketChannel, MessageHandler handler) {
        this.socketChannel = socketChannel;
        this.handler = handler;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public MessageHandler getHandler() {
        return handler;
    }

    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }

    public void addMessage(Message message) {
        messages.offer(message);
    }

    public Queue<Message> getMessages() {
        return messages;
    }
}
