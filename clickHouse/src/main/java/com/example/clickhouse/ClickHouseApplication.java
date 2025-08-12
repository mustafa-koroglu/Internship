package com.example.clickhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClickHouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickHouseApplication.class, args);
        System.out.println("ClickHouse Student Management Application started successfully!");
    }
}
