package com.daniel.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.daniel.entity.*;

import jakarta.persistence.Column;

public class TransportUtil {


    private final static Random random = new Random();
    private final static int CAR_COUNT_LIMIT=10;
    private final static int SEAT_COUNT_LIMIT = 1000;
    private final static Logger logger = Logger.getLogger(TransportUtil.class.getName());
    
    public static Map<String,Object> preFill(){
        logger.info("Beggining generation");
        return generateSeats();
    }
    private static Map<String,Object> generateSeats(){
    int seatCount =SEAT_COUNT_LIMIT;
    SeatEntity [] seats = new SeatEntity[seatCount];
    boolean available = true;
    String [] seatTypeChoices = {"window","middle","aisle"};

    //build seat
    for(int i=0;i<seatCount;i++){
        seats[i]=new SeatEntity(
          available,
          seatTypeChoices[random.nextInt(seatTypeChoices.length)]
        );
    }
    //get cars
    CarEntity [] cars = generateCars(seatCount);
    //combine sets abd cars
    for(int i=0;i<seatCount;i++){
        seats[i].setCarId(
            cars[random.nextInt(cars.length)].getCarId()
        );
    }

    //build map
    Map<String,Object> tools = new HashMap<>();
    tools.put("cars",cars);
    tools.put("seats",seats);

    return tools;

    }

    private static CarEntity [] generateCars(int carCount){
        carCount = CAR_COUNT_LIMIT;
        CarEntity [] cars = new CarEntity[carCount];
        String [] platesPrefixes = {"A","B","C","D","E","F"};
        //int numberOfSeats = 10+random.nextInt(50);
        String [] carTypeChoises = {"Bus","shuttle"};

        for(int i=0;i<carCount;i++){
          cars[i]=new CarEntity(
            ("K"+
             platesPrefixes[random.nextInt(platesPrefixes.length)]+
             platesPrefixes[random.nextInt(platesPrefixes.length)]+ " " + 
             //platesPrefixes[random.nextInt(platesPrefixes.length)]+
             //platesPrefixes[random.nextInt(platesPrefixes.length)]+
             random.nextInt(1000,9999) +
             platesPrefixes[random.nextInt(platesPrefixes.length)]
            ),
            10+random.nextInt(50)
            ,(
            carTypeChoises[random.nextInt(carTypeChoises.length)]
            )
          );
        }

        return cars;
    }
}
