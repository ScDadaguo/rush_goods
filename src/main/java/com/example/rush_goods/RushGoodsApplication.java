package com.example.rush_goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Repository;

@SpringBootApplication
@MapperScan(
        basePackages ="com.example.rush_goods",
        annotationClass = Repository.class
)
@EnableScheduling
public class RushGoodsApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RushGoodsApplication.class);
    }



    public static void main(String[] args) {
        SpringApplication.run(RushGoodsApplication.class, args);
    }


}

