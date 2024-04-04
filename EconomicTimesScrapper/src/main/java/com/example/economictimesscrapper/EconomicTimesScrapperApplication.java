package com.example.economictimesscrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EconomicTimesScrapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(EconomicTimesScrapperApplication.class, args);
    }

}
