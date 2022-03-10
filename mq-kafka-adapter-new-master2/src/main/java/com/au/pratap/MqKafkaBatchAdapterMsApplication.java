package com.au.pratap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan("com.au.pratap.config")
public class MqKafkaBatchAdapterMsApplication {


    public static void main(String[] args) {
        SpringApplication.run(MqKafkaBatchAdapterMsApplication.class, args);
    }

}
