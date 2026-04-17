package com.localwarehouse.backend.config;

import java.io.File;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataDirectoryInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
}
