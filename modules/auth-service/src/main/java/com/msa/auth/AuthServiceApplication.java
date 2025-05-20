package com.msa.auth;

import com.msa.common.constant.MsaConstant.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Slf4j
@EnableR2dbcRepositories(basePackages = {Service.AUTH, Service.COMMON})
@ComponentScan(basePackages = {Service.AUTH, Service.COMMON})
@SpringBootApplication
public class AuthServiceApplication implements ApplicationRunner {
    @Value("${test.name}")
    private String exampleName;

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("exampleName : {} ", exampleName);
    }
}
