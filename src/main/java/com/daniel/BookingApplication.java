package com.daniel;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.daniel.entity.CarEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.repository.CarRepository;
import com.daniel.repository.SeatRepository;
import com.daniel.util.TransportUtil;

@SpringBootApplication
@EnableAsync
public class BookingApplication {

    private static final Logger LOGGER = Logger.getLogger(BookingApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
        LOGGER.info("Booking Application successfully started.");
    }

    /**
     * Safely seeds the database on startup only if it is empty.
     * Prevents duplicate key exceptions on subsequent application restarts.
     */
    @Bean
    public CommandLineRunner dataInitializer(CarRepository carRepository, SeatRepository seatRepository) {
        return args -> {
            if (carRepository.count() > 0) {
                LOGGER.info("Database already contains records. Skipping pre-fill data generation.");
                return;
            }

            LOGGER.info("Beginning database pre-fill initialization...");
            Map<String, Object> generatedData = TransportUtil.preFill();

            SeatEntity[] seats = (SeatEntity[]) generatedData.get("seats");
            CarEntity[] cars = (CarEntity[]) generatedData.get("cars");

            for (CarEntity car : cars) {
                carRepository.save(car);
            }

            for (SeatEntity seat : seats) {
                seatRepository.save(seat);
            }

            LOGGER.info(() -> String.format("Successfully seeded %d cars and %d seats.", cars.length, seats.length));
        };
    }
}