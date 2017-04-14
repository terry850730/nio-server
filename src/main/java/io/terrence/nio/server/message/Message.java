package io.terrence.nio.server.message;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public interface Message {

    ByteBuffer getByteBuffer() throws IOException;
}
