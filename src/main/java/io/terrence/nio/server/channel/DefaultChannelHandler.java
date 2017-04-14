package io.terrence.nio.server.channel;

import io.terrence.nio.server.handler.MessageHandler;
import io.terrence.nio.server.handler.MessageHandlerFactory;
import io.terrence.nio.server.handler.MessageProcessor;
import io.terrence.nio.server.handler.MessageResponse;
import io.terrence.nio.server.message.Message;
import io.terrence.nio.server.pool.ExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Terrence on 2017/4/12.
 * Desc:
 */
public class DefaultChannelHandler implements ChannelHandler {

    public static final int CAPACITY = 5 * 1024;

    private class DefaultMessageResponse implements MessageResponse {

        NioChannel channel;

        DefaultMessageResponse(NioChannel channel) {
            this.channel = channel;
        }

        @Override
        public void write(Message message) {
            if (message != null) {
                channel.addMessage(message);
                try {
                    SocketChannel socketChannel = channel.getSocketChannel();
                    SelectionKey key = socketChannel.keyFor(writeSelector);
                    if (key == null) {
                        key = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
                        key.attach(channel);
                    }
                } catch (ClosedChannelException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        public void close() {
            SocketChannel socketChannel = channel.getSocketChannel();
            SelectionKey key = socketChannel.keyFor(writeSelector);
            key.cancel();
        }
    }

    private static Logger logger = LoggerFactory.getLogger(DefaultChannelHandler.class);
    private ExecutorGroup group = new ExecutorGroup();
    private BlockingQueue<SocketChannel> queue;
    private MessageHandlerFactory factory;

    private Selector readSelector;
    private Selector writeSelector;

    public DefaultChannelHandler(int queueSize, MessageHandlerFactory factory) {
        this.queue = new ChannelQueue(queueSize);
        this.factory = factory;
    }

    @Override
    public void init() throws IOException {
        readSelector = Selector.open();
        writeSelector = Selector.open();
        group.execute(() -> {
            Thread.currentThread().setName("RegisterChannelToReadSelector");
            while (true) {
                try {
                    registerToRead();
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }
            }
        });
        group.execute(() -> {
            Thread.currentThread().setName("ReadPolling");
            while (true) {
                try {
                    read();
                    Thread.sleep(100);
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }
            }
        });
        group.execute(() -> {
            Thread.currentThread().setName("WritePolling");
            while (true) {
                try {
                    write();
                    Thread.sleep(100);
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }
            }
        });
    }

    @Override
    public void addChannel(SocketChannel socketChannel) {
        queue.offer(socketChannel);
    }

    private void registerToRead() throws InterruptedException {
        SocketChannel socketChannel = queue.take();
        while (socketChannel != null) {
            try {
                SelectionKey key = socketChannel.register(readSelector, SelectionKey.OP_READ);
                key.attach(new NioChannel(socketChannel, factory.getHandler()));
            } catch (ClosedChannelException e) {
                logger.error(e.getMessage(), e);
            }
            socketChannel = queue.poll();
        }
    }

    private void read() throws IOException {
        int readReady = readSelector.selectNow();
        if (readReady > 0) {
            Set<SelectionKey> selectionKeys = readSelector.selectedKeys();
            Iterator<SelectionKey> iter = selectionKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                read((NioChannel) key.attachment());
                iter.remove();
            }
        }
    }

    private void read(NioChannel channel) {
        group.execute(() -> {
            Thread.currentThread().setName("ReadAndProcess");
            ByteBuffer byteBuffer = ByteBuffer.allocate(CAPACITY);
            SocketChannel socketChannel = channel.getSocketChannel();

            MessageHandler handler = channel.getHandler();
            MessageProcessor processor = handler.getProcessor();

            int bytesRead = 0;
            DefaultMessageResponse messageResponse = new DefaultMessageResponse(channel);
            try {
                bytesRead = socketChannel.read(byteBuffer);
                while (bytesRead > 0) {
                    bytesRead = socketChannel.read(byteBuffer);
                }
            } catch (IOException e) {
                processor.onError(e, messageResponse);
            }
            processor.onMessage(handler.getReader().read(byteBuffer), messageResponse);
        });
    }

    private void write() throws IOException {
        int writeReady = writeSelector.selectNow();
        if (writeReady > 0) {
            Set<SelectionKey> selectionKeys = writeSelector.selectedKeys();
            Iterator<SelectionKey> iter = selectionKeys.iterator();
            while (iter.hasNext()) {
                final SelectionKey key = iter.next();
                group.execute(() -> {
                    Thread.currentThread().setName("WriteToChannel");
                    NioChannel channel = (NioChannel) key.attachment();
                    SocketChannel socketChannel = channel.getSocketChannel();
                    Queue<Message> messages = channel.getMessages();
                    Message message = messages.poll();
                    while (message != null) {
                        try {
                            ByteBuffer byteBuffer = message.getByteBuffer();
                            byteBuffer.flip();
                            int bytesWritten = socketChannel.write(byteBuffer);
                            while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
                                bytesWritten = socketChannel.write(byteBuffer);
                            }
                            byteBuffer.clear();
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                        message = messages.poll();
                    }
                });
                iter.remove();
            }
        }
    }
}
