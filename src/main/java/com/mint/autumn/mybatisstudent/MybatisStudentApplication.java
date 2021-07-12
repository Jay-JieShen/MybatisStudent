package com.mint.autumn.mybatisstudent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mint.autumn.mybatisstudent.mapper")
@SpringBootApplication
public class MybatisStudentApplication {

    public static void main(String[] args) {

        SpringApplication.run(MybatisStudentApplication.class, args);
    }

}
