package me.dehasi.highload.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import me.dehasi.highload.Accounts;
import me.dehasi.highload.service.Repository;

/** Created by Ravil on 17/12/2018. */
public class GroupHandler  implements HttpHandler {

    private final Repository repository;
    private final ObjectMapper objectMapper;

    public GroupHandler(Repository repository, ObjectMapper mapper) {
        this.repository = repository;
        objectMapper = mapper;
    }

    @Override public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        byte[] response = objectMapper.writeValueAsBytes(null);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
