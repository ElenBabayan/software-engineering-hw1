package com.example.nutssubscriber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NutsSubscriberApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutsSubscriberApplication.class, args);
    }

}
