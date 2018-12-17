package me.dehasi.highload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Created by Ravil on 16/12/2018. */
public class Accounts {
    public List<Account> accounts;

    @JsonCreator
    public Accounts(@JsonProperty("accounts") List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override public String toString() {
        return "Accounts{" +
            "accounts=" + accounts +
            '}';
    }
}
