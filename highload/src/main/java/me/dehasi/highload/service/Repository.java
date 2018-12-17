package me.dehasi.highload.service;

import java.util.Optional;
import java.util.stream.Stream;
import me.dehasi.highload.Account;

public interface Repository {
    void save(Account account);

    void update(Account account);

    Stream<Account> findAll();

    Optional<Account> findById(int id);
}
