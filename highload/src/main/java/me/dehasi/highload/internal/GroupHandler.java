package me.dehasi.highload.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.dehasi.highload.Account;
import me.dehasi.highload.Group;
import me.dehasi.highload.Groups;
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

        ParseResult result = parseQuety(query);
        Stream<Account> stream = repository.findAll().filter(result.predicate);
        Function<Account, Group> groupFunction = createGroupFunction(result.params);
        Map<Group, Long> collect = repository.findAll().collect(Collectors.groupingBy(groupFunction, Collectors.counting()));

        Comparator<Map.Entry<Group, Long>> comparator = new Comparator<Map.Entry<Group, Long>>() {
            @Override public int compare(Map.Entry<Group, Long> o1, Map.Entry<Group, Long> o2) {
                return result.order * o1.getValue().compareTo(o2.getValue());
            }
        };
        List<Group> groups = collect.entrySet().stream().sorted(comparator).limit(result.limit).map(entry -> {
            Group key = entry.getKey();
            Group group = new Group();
            group.sex = key.sex;
            group.status = key.status;
            group.interests = key.interests;
            group.country = key.country;
            group.city = key.city;
            group.count = entry.getValue();
            return group;
        }).collect(Collectors.toList());

        byte[] response = objectMapper.writeValueAsBytes(new Groups(groups));

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private Function<Account, Group> createGroupFunction(Set<String> params) {

        return x -> {
            Group group = new Group();
            if (params.contains("sex"))
                group.sex = x.sex;
            if (params.contains("status"))
                group.status = x.status;
            if (params.contains("interests"))
                group.interests = x.interests;
            if (params.contains("country"))
                group.country = x.country;
            if (params.contains("city"))
                group.city = x.city;
            return group;
        };
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
            this.limit = limit > 0 ? limit : 50;
            this.order = order;
            this.params = params;
        }
    }
}
