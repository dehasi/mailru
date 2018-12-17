package me.dehasi.highload.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.dehasi.highload.Account;
import me.dehasi.highload.Group;
import me.dehasi.highload.service.Repository;

import static java.util.Arrays.asList;
import static me.dehasi.highload.internal.Utils.yearFormTs;

/** Created by Ravil on 17/12/2018. */
public class GroupHandler implements HttpHandler {

    private final Repository repository;
    private final ObjectMapper objectMapper;

    public GroupHandler(Repository repository, ObjectMapper mapper) {
        this.repository = repository;
        objectMapper = mapper;
    }

    @Override public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        Map<Group, Long> collect = repository.findAll().collect(Collectors.groupingBy(x -> new Group(), Collectors.counting()));

        byte[] response = objectMapper.writeValueAsBytes(null);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private ParseResult parseQuety(String query) {
        String[] attributes = query.split("&");
        Predicate<Account> p = x -> true;
        Set<String> params = new HashSet<>(attributes.length);

        int limit = -1;
        int order = 0;
        for (int i = 0; i < attributes.length; i++) {
            String[] split = attributes[i].split("=");
            String param = split[0].trim();
            String val = split[1].trim();
            if (param.equals("limit")) {
                limit = Integer.parseInt(val);
                continue;
            }
            if (param.equals("order")) {
                order = Integer.parseInt(val);
                continue;
            }
            if (param.equals("query_id")) {
                continue;
            }
            if (param.equals("keys")) {
                params.addAll(asList(val.split(",")));
                continue;
            }
            p = p.and(createPredicate(param, val));

        }

        return new ParseResult(p, limit, order, params);
    }

    //для likes будет только один id, для interests только одна строка, для birth и joined - будет одно число - год
    private Predicate<? super Account> createPredicate(String param, String val) {
        switch (param) {
            case "likes": {
                List<String> likes = asList(val.split(","));
                return x ->
                    Stream.of(x.likes).map(like -> like.id).map(Objects::toString).anyMatch(likes::contains);
            }
            case "interests":
                return x -> asList(x.interests).contains(val);
            case "birth":
                return x -> yearFormTs(x.birth) == Integer.parseInt(val);
            case "joined":
                return x -> yearFormTs(x.joined) == Integer.parseInt(val);
            default:
                throw new UnknowParamException(param + ":" + val);
        }
    }

    private static class ParseResult {
        Predicate<Account> predicate;
        int limit;
        int order;
        Set<String> params;

        public ParseResult(Predicate<Account> predicate, int limit, int order, Set<String> params) {
            this.predicate = predicate;
            this.limit = limit;
            this.order = order;
            this.params = params;
        }
    }
}
