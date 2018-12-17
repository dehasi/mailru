package me.dehasi.highload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import me.dehasi.highload.service.AccountsLoader;
import me.dehasi.highload.service.HashMapRepository;
import me.dehasi.highload.service.Repository;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Created by Ravil on 16/12/2018. */
public class AccountsLoaderTest {

    Repository repository = new HashMapRepository();
    ObjectMapper objectMapper = new ObjectMapper();
    String path = "A:\\mailru\\highload\\src\\main\\resources\\data\\data.zip";

    AccountsLoader loader;

    @Before
    public void createLoader() {
        loader = new AccountsLoader(repository, objectMapper, path);
    }
    @Test
    public void test1 () throws IOException {
        loader.init();
        assertThat(repository.findById(1)).isNotNull();
    }



}
