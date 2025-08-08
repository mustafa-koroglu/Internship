package com.example.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClickHouseConfig {

    @Value("${clickhouse.datasource.url}")
    private String clickhouseUrl;

    @Value("${clickhouse.datasource.username}")
    private String clickhouseUsername;

    @Value("${clickhouse.datasource.password}")
    private String clickhousePassword;

    @Bean(name = "clickhouseDataSource")
    public DataSource clickhouseDataSource() {
        // Basit DataSource oluştur
        org.springframework.jdbc.datasource.SimpleDriverDataSource dataSource =
                new org.springframework.jdbc.datasource.SimpleDriverDataSource();

        dataSource.setDriverClass(com.clickhouse.jdbc.ClickHouseDriver.class); //basit bağlantılar kurmak için
        dataSource.setUrl(clickhouseUrl);
        dataSource.setUsername(clickhouseUsername);
        dataSource.setPassword(clickhousePassword);

        return dataSource;
    }

    @Bean(name = "clickhouseJdbcTemplate")
    public JdbcTemplate clickhouseJdbcTemplate(@Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource); // sql sorgularını kolayca çalıştırmaya yarar
    }
}