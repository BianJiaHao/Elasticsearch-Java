package com.bianjiahao.es;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bianjiahao.es.dao")
public class EsJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsJavaApplication.class, args);
    }

}
