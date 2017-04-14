package io.terrence.nio.server.http;

import java.util.Arrays;

/**
 * Created by Terrence on 2017/4/14.
 * Desc:
 */
public class HttpUtil {

    public static final String UTF_8 = "UTF-8";

    public static HttpMessage parseHttpRequest(byte[] bytes, int startIndex, int endIndex) {
        HttpMessage httpMessage = new HttpMessage();
        HttpHeader header = new HttpHeader();
        int index = findBodyIndex(bytes, startIndex, endIndex);
        header.parse(bytes, startIndex, index);
        httpMessage.setHeader(header);

        if (index > -1 && index < endIndex) {
            System.out.println(index + ": " + endIndex);
            httpMessage.setBody(Arrays.copyOfRange(bytes, index, endIndex));
        }
        return httpMessage;
    }

    private static int findBodyIndex(byte[] bytes, int startIndex, int endIndex) {
        for (int index = startIndex + 3; index < endIndex; index++) {
            if (bytes[index] == '\n' && bytes[index - 1] == '\r' && bytes[index - 2] == '\n' && bytes[index - 3] == '\r') {
                return index + 1;
            }
        }
        return -1;
    }
}
