package io.terrence.nio.server.handler;

import java.nio.ByteBuffer;

/**
 * Created by Terrence on 2017/4/12.
 * Desc:
 */
public interface MessageReader<T> {

    T read(ByteBuffer byteBuffer);

}
