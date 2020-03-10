package com.playground.loyalitypointsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoyaltyPointsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyaltyPointsServiceApplication.class, args);
    }

}
