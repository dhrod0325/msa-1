package com.msa.usersite;

import com.msa.common.constant.MsaConstant.Service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories(basePackages = {"com.msa.usersite", Service.COMMON})
@ComponentScan(basePackages = {"com.msa.usersite", Service.COMMON})
@SpringBootApplication
public class UserSiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserSiteApplication.class, args);
    }
}