package com.localwarehouse.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.localwarehouse.backend.module.**.mapper")
public class LocalWarehouseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalWarehouseBackendApplication.class, args);
    }
}
