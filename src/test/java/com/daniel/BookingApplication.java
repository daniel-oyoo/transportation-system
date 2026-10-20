package com.daniel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.daniel.entity.SeatEntity;
import com.daniel.repository.SeatRepository;

import jakarta.persistence.PostLoad;

@SpringBootApplication
@EnableAsync
public class BookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
        System.out.println("Travel Booking System Started Successfully!");
    }
}