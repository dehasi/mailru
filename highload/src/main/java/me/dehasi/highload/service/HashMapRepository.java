package me.dehasi.highload.service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import me.dehasi.highload.Account;
import org.springframework.stereotype.Component;

@Component
public class HashMapRepository implements Repository {

    private final ConcurrentHashMap<Integer, Account> accounts = new ConcurrentHashMap<>();

    @Override public void save(Account account) {
        accounts.put(account.id, account);
    }

    @Override public void update(Account account) {
        //TODO: add merger
        accounts.put(account.id, account);
    }

    @Override public Stream<Account> findAll() {
        return accounts.values().stream();
    }

    @Override public Optional<Account> findById(int id) {
        return Optional.ofNullable(accounts.get(id));
    }
}
