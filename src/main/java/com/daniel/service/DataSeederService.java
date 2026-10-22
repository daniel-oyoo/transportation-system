package com.daniel.service;

import com.daniel.entity.CarEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.repository.CarRepository;
import com.daniel.repository.SeatRepository;
import com.daniel.util.TransportUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class DataSeederService {

    private static final Logger LOGGER = Logger.getLogger(DataSeederService.class.getName());

    private final CarRepository carRepository;
    private final SeatRepository seatRepository;

    public DataSeederService(CarRepository carRepository, SeatRepository seatRepository) {
        this.carRepository = carRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Runs asynchronously on the 'requestExecutor' thread pool.
     */
    @Async("requestExecutor")
    public CompletableFuture<Long> seedDataAsync() {
        LOGGER.info(() -> "Starting async data seeding on thread: " + Thread.currentThread().getName());

        Map<String, Object> generatedData = TransportUtil.preFill();
        SeatEntity[] seats = (SeatEntity[]) generatedData.get("seats");
        CarEntity[] cars = (CarEntity[]) generatedData.get("cars");

        // Batch save for max efficiency
        carRepository.saveAll(Arrays.asList(cars));
        seatRepository.saveAll(Arrays.asList(seats));

        long totalCount = carRepository.count() + seatRepository.count();
        LOGGER.info(() -> "Async seeding completed. Total records in DB: " + totalCount);

        return CompletableFuture.completedFuture(totalCount);
    }
}