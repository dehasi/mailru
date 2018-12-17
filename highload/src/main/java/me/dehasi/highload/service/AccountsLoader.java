package me.dehasi.highload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.PostConstruct;
import me.dehasi.highload.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class AccountsLoader {

    private final Repository repository;
    private final ObjectMapper objectMapper;
    private final String filePath;
    @Autowired ResourceLoader loader;

    public AccountsLoader(Repository repository, ObjectMapper objectMapper, @Value("${zip.path}") String filePath) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.filePath = filePath;
        System.err.println(filePath);
    }

    @PostConstruct
    public void init() {
        System.err.println("read zip");
        try {
            readZipFile(filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readZipFile(String fileName) throws IOException {
        try (ZipFile zipFile = new ZipFile(fileName)) {
            // ZipFile offers an Enumeration of all the files in the Zip file
            for (Enumeration e = zipFile.entries(); e.hasMoreElements(); ) {
                ZipEntry zipEntry = (ZipEntry)e.nextElement();
                String entryName = zipEntry.getName();
                if (entryName.startsWith("accounts_") && entryName.endsWith(".json")) {
                    Accounts accounts = objectMapper.readValue(zipFile.getInputStream(zipEntry), Accounts.class);
                    System.err.println(accounts);
                    accounts.accounts.forEach(repository::save);
                }
            }
        }

        catch (IOException ioe) {
            System.out.println("An IOException occurred: " + ioe.getMessage());
        }
    }

}
