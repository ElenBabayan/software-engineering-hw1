package com.example.natssubscriber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NatsSubscriberApplication {

    public static void main(String[] args) {
        SpringApplication.run(NatsSubscriberApplication.class, args);
    }

}
