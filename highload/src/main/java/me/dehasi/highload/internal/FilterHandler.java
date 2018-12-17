package me.dehasi.highload.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.dehasi.highload.Account;
import me.dehasi.highload.Accounts;
import me.dehasi.highload.service.Repository;

/** Created by Ravil on 16/12/2018. */
public class FilterHandler implements HttpHandler {

    private final Repository repository;
    private final ObjectMapper objectMapper;

    public FilterHandler(Repository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        ParseResult parseResult = parseFilterQuery(query);
        Stream<Account> stream = repository.findAll().filter(parseResult.predicate);

        if (parseResult.limit > 0) {
            stream = stream.limit(parseResult.limit);
        }
        List<Account> accounts = stream.map(a -> cutResponse(a, parseResult.params)).collect(Collectors.toList());

        byte[] response = objectMapper.writeValueAsBytes(new Accounts(accounts));

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    private ParseResult parseFilterQuery(String query) {
        String[] attributes = query.split("&");
        Predicate<Account> p = x -> true;
        Set<String> params = new HashSet<>(attributes.length);

        int limit = -1;
        for (int i = 0; i < attributes.length; i++) {
            String[] split = attributes[i].split("=");
            String param = split[0].trim();
            String val = split[1].trim();
            if (param.equals("limit")) {
                limit = Integer.parseInt(val);
                continue;
            }
            if (param.equals("query_id")) {
                continue;
            }

            params.add(param.substring(0, param.indexOf('_')));
            p = p.and(createPredicate(param, val));
        }
        return new ParseResult(p, limit, params);
    }

    Predicate<Account> createPredicate(String comp, String val) {
        switch (comp) {
            case "sex_m":
                return x -> x.sex.equals(val);
            case "email_domain":
                return x -> x.email.endsWith(val);
            case "email_lt":
                return x -> x.email.compareTo(val) > 0;
            case "email_gt":
                return x -> x.email.compareTo(val) < 0;
            case "status_eq":
                return x -> x.status.equals(val);
            case "status_neq":
                return x -> !x.status.equals(val.replace('+', ' '));
            case "fname_eq":
                return x -> x.fname.equals(val);
            case "fname_any":
                return x -> Arrays.asList(val.split(",")).contains(x.fname);
            case "fname_null":
                return val.contains("1") ? x -> x.fname == null : x -> x.fname != null;
            case "sname_eq":
                return x -> x.sname.equals(val);
            case "sname_starts":
                return x -> x.sname.startsWith(val);
            case "sname_null":
                return val.contains("1") ? x -> x.sname == null : x -> x.sname != null;
            case "phone_code":
                return x -> x.phone != null && x.phone.contains(val);
            case "phone_null":
                return val.contains("1") ? x -> x.phone == null : x -> x.phone != null;
            case "country_eq":
                return x -> x.country != null && x.country.equals(val);
            case "country_null":
                return val.contains("1") ? x -> x.country == null : x -> x.country != null;
            case "city_eq":
                return x -> x.city != null && x.city.equals(val);
            case "city_any":
                return x -> Arrays.asList(val.split(",")).contains(x.city);
            case "city_null":
                return val.contains("1") ? x -> x.city == null : x -> x.city != null;
            case "birth_lt":
                return x -> x.birth > Integer.parseInt(val);
            case "birth_gt":
                return x -> x.birth < Integer.parseInt(val);
            case "birth_year":
                return x -> yearFormTs(x.birth) == Integer.parseInt(val);
            case "interests_contains":
                return x -> Arrays.asList(x.interests).containsAll(Arrays.asList(val.split(",")));
            case "interests_any":
                return x -> !Collections.disjoint(Arrays.asList(x.interests), Arrays.asList(val.split(",")));
            case "likes_contains": {
                List<String> likes = Arrays.asList(val.split(","));
                return x ->
                    Stream.of(x.likes).map(like -> like.id).map(Objects::toString).allMatch(likes::contains);
            }
            case "premium_now":
                return x -> x.premium != null && x.premium.finish > Instant.now().getEpochSecond();
            case "premium_null":
                return val.contains("1") ? x -> x.premium == null : x -> x.premium != null;
            default:
                return x -> true;
        }
    }

    private int yearFormTs(long ts) {
        return Instant.ofEpochSecond(ts).atZone(ZoneId.of("UTC")).getYear();
    }

    private Account cutResponse(Account account, Set<String> params) {
        Account result = new Account();

        result.id = account.id;
        result.email = account.email;

        if (params.contains("fname"))
            result.fname = account.fname;
        if (params.contains("sname"))
            result.sname = account.sname;
        if (params.contains("phone"))
            result.phone = account.phone;
        if (params.contains("sex"))
            result.sex = account.sex;
        if (params.contains("status"))
            result.status = account.status;
        if (params.contains("birth"))
            result.birth = account.birth;
        if (params.contains("country"))
            result.country = account.country;
        if (params.contains("city"))
            result.city = account.city;
        if (params.contains("interests"))
            result.interests = account.interests;
        if (params.contains("likes"))
            result.likes = account.likes;
        if (params.contains("premium"))
            result.premium = account.premium;

        return result;
    }

    static class ParseResult {
        Predicate<Account> predicate;
        int limit = -1;
        Set<String> params;

        public ParseResult(Predicate<Account> predicate, int limit, Set<String> params) {
            this.predicate = predicate;
            this.limit = limit;
            this.params = params;
        }
    }

}
