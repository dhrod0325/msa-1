package com.msa.auth;

import com.msa.common.constant.MsaConstant.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@ComponentScan(basePackages = {Service.AUTH, Service.COMMON})
@EnableJpaRepositories(basePackages = {Service.AUTH, Service.COMMON})
@EntityScan(basePackages = {Service.AUTH, Service.COMMON})
@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
