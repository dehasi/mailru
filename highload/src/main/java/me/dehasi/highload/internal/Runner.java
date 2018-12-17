package me.dehasi.highload.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import me.dehasi.highload.service.AccountsLoader;
import me.dehasi.highload.service.HashMapRepository;
import me.dehasi.highload.service.Repository;

public class Runner {

    private void run() throws IOException {
        Repository repository = new HashMapRepository();
        ObjectMapper objectMapper = new ObjectMapper();
        String path = "A:\\mailru\\highload\\src\\main\\resources\\data\\data.zip";
        AccountsLoader loader = new AccountsLoader(repository, objectMapper, path);
        loader.init();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.start();

        server.createContext("/accounts/filter", new FilterHandler(repository, objectMapper));
        server.createContext("/accounts/group", new GroupHandler(repository, objectMapper));

    }

    public static void main(String[] args) throws IOException {
        new Runner().run();
    }
}
