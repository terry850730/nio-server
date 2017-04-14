package io.terrence.nio.server.http;

import java.io.UnsupportedEncodingException;

import static io.terrence.nio.server.http.HttpUtil.UTF_8;

/**
 * Created by Terrence on 2017/4/14.
 * Desc:
 */
public class HttpHeader {

    public static final String LINE_BREAK = "\r\n";

    public enum HttpMethod {
        GET(new byte[] { 'G', 'E', 'T' }),
        POST(new byte[] { 'P', 'O', 'S', 'T' }),
        PUT(new byte[] { 'P', 'U', 'T' }),
        DELETE(new byte[] { 'D', 'E', 'L', 'E', 'T', 'E' });

        private byte[] chars;

        HttpMethod(byte[] chars) {
            this.chars = chars;
        }
    }

    public HttpMethod method;
    public String url;
    public String version;
    public int contentLength;

    public byte[] toBytes() {
        // TODO 需要从各字段中生成头信息
        String head = "HTTP/1.1 200 OK" + LINE_BREAK +
            "Content-Length: " + contentLength + LINE_BREAK +
            "Content-Type: text/html" + LINE_BREAK + LINE_BREAK;

        byte[] bytes = null;
        try {
            bytes = head.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void parse(byte[] bytes, int startIndex, int endIndex) {
        try {
            if (startIndex >= endIndex) {
                return;
            }
            int index = parseMethod(bytes, startIndex);
            index = parseUrlAndVersion(bytes, index + 1, endIndex) + 1;
            // TODO 解析其他字段
            while (index < endIndex) {
                int nextLine = findLineBreak(bytes, index, endIndex);
                if (nextLine <= index) {
                    break;
                }
                String line = new String(bytes, index, nextLine - 2 - index + 1, UTF_8);
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.substring(line.indexOf(":")));
                    break;
                }
                index = nextLine + 1;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private int parseUrlAndVersion(byte[] bytes, int startIndex, int endIndex) throws UnsupportedEncodingException {
        int nextIndex = findLineBreak(bytes, startIndex, endIndex);
        String line = new String(bytes, startIndex, nextIndex - 2 - startIndex + 1, UTF_8);
        String[] s = line.split(" ");
        url = s[0];
        version = s[1];
        return nextIndex;
    }

    private int parseMethod(byte[] bytes, int startIndex) {
        if (matches(bytes, startIndex, HttpMethod.GET.chars)) {
            method = HttpMethod.GET;
            return HttpMethod.GET.chars.length;
        } else if (matches(bytes, startIndex, HttpMethod.POST.chars)) {
            method = HttpMethod.POST;
            return HttpMethod.POST.chars.length;
        } else if (matches(bytes, startIndex, HttpMethod.PUT.chars)) {
            method = HttpMethod.PUT;
            return HttpMethod.PUT.chars.length;
        } else if (matches(bytes, startIndex, HttpMethod.DELETE.chars)) {
            method = HttpMethod.DELETE;
            return HttpMethod.DELETE.chars.length;
        }
        // TODO 解析其他类型的报文
        return -1;
    }

    private static int findLineBreak(byte[] bytes, int startIndex, int endIndex) {
        for (int index = startIndex; index < endIndex; index++) {
            if (bytes[index] == '\n' && bytes[index - 1] == '\r') {
                return index;
            }
        }
        return -1;
    }

    private static boolean matches(byte[] src, int offset, byte[] value) {
        for (int i = offset, n = 0; n < value.length; i++, n++) {
            if (src[i] != value[n]) {
                return false;
            }
        }
        return true;
    }
}
