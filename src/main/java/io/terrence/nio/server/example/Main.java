package io.terrence.nio.server.example;

import io.terrence.nio.server.Server;
import io.terrence.nio.server.http.HttpMessageHandler;

import java.io.IOException;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public class Main {

    public static void main(String... s) throws IOException {
        Server server = new Server(5555, HttpMessageHandler::new);
        server.start();
    }

}
