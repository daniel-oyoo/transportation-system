package com.daniel.util;

import com.daniel.entity.CarEntity;
import com.daniel.entity.SeatEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public final class TransportUtil {

    private static final Logger LOGGER = Logger.getLogger(TransportUtil.class.getName());

    private static final int CAR_COUNT_LIMIT = 10;
    private static final int SEAT_COUNT_LIMIT = 1000;
    private static final int MIN_CAR_CAPACITY = 10;
    private static final int MAX_CAR_CAPACITY = 60; // 10 + 50 bound

    private static final String[] SEAT_TYPE_CHOICES = {"window", "middle", "aisle"};
    
    // Expanded from A to Z
    private static final String[] PLATE_PREFIXES = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
        "U", "V", "W", "X", "Y", "Z"
    };
    
    private static final String[] CAR_TYPE_CHOICES = {"Bus", "shuttle"};

    // Prevent instantiation of utility class
    private TransportUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Pre-fills transport data containing generated cars and seats.
     * 
     * @return A map containing "cars" and "seats" arrays.
     */
    public static Map<String, Object> preFill() {
        LOGGER.info("Beginning transport data generation...");
        return generateSeats();
    }

    private static Map<String, Object> generateSeats() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Build seats stream-based for elegance
        SeatEntity[] seats = IntStream.range(0, SEAT_COUNT_LIMIT)
                .mapToObj(i -> new SeatEntity(
                        true,
                        SEAT_TYPE_CHOICES[random.nextInt(SEAT_TYPE_CHOICES.length)]
                ))
                .toArray(SeatEntity[]::new);

        // Generate cars
        CarEntity[] cars = generateCars();

        // Assign seats to random cars
        for (SeatEntity seat : seats) {
            CarEntity randomCar = cars[random.nextInt(cars.length)];
            seat.setCarId(randomCar.getCarId());
        }

        // Construct result map
        Map<String, Object> tools = new HashMap<>(2);
        tools.put("cars", cars);
        tools.put("seats", seats);

        LOGGER.info(() -> String.format("Successfully generated %d cars and %d seats.", cars.length, seats.length));
        return tools;
    }

    private static CarEntity[] generateCars() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        CarEntity[] cars = new CarEntity[CAR_COUNT_LIMIT];

        for (int i = 0; i < CAR_COUNT_LIMIT; i++) {
            String licensePlate = buildLicensePlate(random);
            int capacity = random.nextInt(MIN_CAR_CAPACITY, MAX_CAR_CAPACITY);
            String carType = CAR_TYPE_CHOICES[random.nextInt(CAR_TYPE_CHOICES.length)];

            cars[i] = new CarEntity(licensePlate, capacity, carType);
        }

        return cars;
    }

    private static String buildLicensePlate(ThreadLocalRandom random) {
        String p1 = PLATE_PREFIXES[random.nextInt(PLATE_PREFIXES.length)];
        String p2 = PLATE_PREFIXES[random.nextInt(PLATE_PREFIXES.length)];
        String p3 = PLATE_PREFIXES[random.nextInt(PLATE_PREFIXES.length)];
        int digits = random.nextInt(1000, 10000);

        return "K" + p1 + p2 + " " + digits + p3;
    }
}