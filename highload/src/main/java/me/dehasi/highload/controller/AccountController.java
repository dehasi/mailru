package me.dehasi.highload.controller;

import java.util.List;
import me.dehasi.highload.Account;
import me.dehasi.highload.Accounts;
import me.dehasi.highload.service.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

//@RestController("accounts/")
public class AccountController {

    private final Repository repository;

    public AccountController(Repository repository) {
        this.repository = repository;
    }

    @GetMapping("filter/")
    public Accounts filter(@RequestParam(name = "sex_eq", required = false) String sex_eq) {
        List<Account> accounts = repository.findAll().filter(a -> a.sex.equals(sex_eq)).collect(toList());
        return new Accounts(accounts);
    }

    @GetMapping("group")
    public Accounts group() {
        return null;
    }

    @GetMapping("{id}/recommend")
    public Accounts recommend(@PathVariable(name = "id", required = true) int id) {
        return null;
    }

    @GetMapping("{id}/suggest")
    public Accounts suggest(@PathVariable("id") int id) {
        return null;
    }

    // --
    @PostMapping("{id}")
    public String update(@PathVariable("id") int id, @RequestBody Account account) {
        return "3";
    }

    @PostMapping("new")
    public String recommend(@RequestBody Account account) {
        return "32";
    }

    @PostMapping("likes")
    public String likes() {
        return "12";
    }

}

