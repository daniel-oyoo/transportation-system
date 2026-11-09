package com.daniel;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingApplication {

    private static final Logger LOGGER = Logger.getLogger(BookingApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
        LOGGER.info("Booking Application successfully started.");
    }
}