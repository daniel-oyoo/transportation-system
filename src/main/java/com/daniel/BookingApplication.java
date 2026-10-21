package com.daniel;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.daniel.repository.*;
import com.daniel.util.TransportUtil;
import com.daniel.entity.*;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableAsync
public class BookingApplication {
    private static final Logger logger = Logger.getLogger(BookingApplication.class.getName());
    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
        logger.info("Live {}");
        logger.info("Lepoooo...");
       // fillRepo();
    }

    //@PostConstruct 
    public static void fillRepo(){
        var context = new AnnotationConfigApplicationContext(BookingApplication.class);
        CarRepository r = context.getBean(CarRepository.class);
        SeatRepository r1= context.getBean(SeatRepository.class);

        SeatEntity [] seats =(SeatEntity [])TransportUtil.preFill().get("seats");
        CarEntity []  cars =(CarEntity[])TransportUtil.preFill().get("cars");

        for(SeatEntity s : seats){
            logger.info("Saving seat with id " + s.getCarId());
            r1.save(s);
            // logger.info("Saving seat {}" + s);
            logger.info("Seat count " + r1.count());
        }
        for(CarEntity c : cars){
            logger.info("Saving car with id " + c.getCarId());
            //logger.info("Saving car {}" + c);
            r.save(c);
            logger.info("Car count " + r.count());
        }

        logger.info("Al seats " + r1.findAll());
         logger.info("Al cars " + r.findAll());
        context.close();
    }


}

@Component 
class Fill{
    private final CarRepository r;

    private final SeatRepository r1;

    @Autowired
    public Fill(
        CarRepository carRepository,
      SeatRepository seatRepository
    ) {
        this.r =carRepository;
        this.r1=seatRepository;
    }
    @PostConstruct 
    public void fillRepo(){
        //var context = new AnnotationConfigApplicationContext(BookingApplication.class);
        //CarRepository r = context.getBean(CarRepository.class);
        //SeatRepository r1= context.getBean(SeatRepository.class);

        Map<String,Object>carsNseats = TransportUtil.preFill();

        //two calls distorts our view
        //SeatEntity [] seats =(SeatEntity []) TransportUtil.preFill().get("seats");
        //CarEntity []  cars =(CarEntity[]) TransportUtil.preFill().get("cars");
        SeatEntity [] seats=(SeatEntity []) carsNseats.get("seats");
        CarEntity []  cars =(CarEntity[]) carsNseats.get("cars");


        for(SeatEntity s :seats){r1.save(s);}
        for(CarEntity c : cars){r.save(c);}

        ///context.close();
    }
}