package com.daniel.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.daniel.entity.CarEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.entity.TripEntity;
import com.daniel.repository.CarRepository;
import com.daniel.repository.SeatRepository;
import com.daniel.repository.TripRepository;

@Configuration
@Profile("dev")
public class DemoDataConfig {

    private static final ZoneId KENYA_TIME_ZONE = ZoneId.of("Africa/Nairobi");

    @Bean
    CommandLineRunner seedDemoData(CarRepository cars, SeatRepository seats, TripRepository trips) {
        return args -> {
            if (trips.count() > 0) {
                return;
            }

            CarEntity kisumuBus = cars.save(new CarEntity("DEMO 101A", 33, "Coach"));
            CarEntity mombasaBus = cars.save(new CarEntity("DEMO 202B", 45, "Coach"));
            seats.saveAll(createSeats(kisumuBus, 33));
            seats.saveAll(createSeats(mombasaBus, 45));

            LocalDate tomorrow = LocalDate.now(KENYA_TIME_ZONE).plusDays(1);
            trips.saveAll(List.of(
                    trip("Nairobi - Kisumu", "Nairobi", "Kisumu",
                            tomorrow.atTime(7, 0), tomorrow.atTime(14, 30), "1200.00", kisumuBus),
                    trip("Nairobi - Mombasa", "Nairobi", "Mombasa",
                            tomorrow.atTime(8, 0), tomorrow.atTime(18, 0), "1800.00", mombasaBus),
                    trip("Nairobi - Kisumu", "Nairobi", "Kisumu",
                            tomorrow.plusDays(1).atTime(7, 0), tomorrow.plusDays(1).atTime(14, 30),
                            "1200.00", kisumuBus),
                    trip("Nairobi - Mombasa", "Nairobi", "Mombasa",
                            tomorrow.plusDays(1).atTime(8, 0), tomorrow.plusDays(1).atTime(18, 0),
                            "1800.00", mombasaBus)));
        };
    }

    private List<SeatEntity> createSeats(CarEntity car, int capacity) {
        List<SeatEntity> seats = new ArrayList<>(capacity);
        String[] types = { "window", "middle", "aisle" };
        for (int i = 0; i < capacity; i++) {
            SeatEntity seat = new SeatEntity(true, types[i % types.length]);
            seat.setCarId(car.getCarId());
            seats.add(seat);
        }
        return seats;
    }

    private TripEntity trip(String route, String from, String to, LocalDateTime departure,
            LocalDateTime arrival, String fare, CarEntity car) {
        return new TripEntity(route, from, to, departure, arrival, new BigDecimal(fare), car.getCarId());
    }
}
