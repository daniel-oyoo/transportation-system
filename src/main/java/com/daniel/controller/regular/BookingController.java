package com.daniel.controller.regular;

import java.net.URI;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.daniel.dto.request.BookingRequest;
import com.daniel.dto.response.BookingResponse;
import com.daniel.service.regular.CustomerService;

import jakarta.persistence.Column;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.daniel.repository.*;
import com.daniel.util.TransportUtil;
import com.daniel.entity.*;
import com.daniel.*;
@RestController
@RequestMapping("api/v1/booking")
public class BookingController {
    private final CustomerService customerService;

    private  final Logger logger = Logger.getLogger(BookingController.class.getName());

    
    //private final CarRepository r;

    //private final SeatRepository r1;
/* 
    @Autowired
    public BookingController(
        CustomerService customerService,
        CarRepository carRepository,
      SeatRepository seatRepository
    ) {
        this.customerService = customerService;
        this.r =carRepository;
        this.r1=seatRepository;
    }
 
*/
    @Autowired
    public BookingController(CustomerService customerService) {
        this.customerService = customerService;
    }
        


    @PostMapping
    public ResponseEntity<BookingResponse> 
    book(@Valid 
        @RequestBody BookingRequest request,
        HttpServletRequest req
    ) {
        logger.info("Received request to book travel {} " + request);
        
        BookingResponse response = customerService.book(request);
        
        return ResponseEntity
                .status(201)
                .location(URI.create(req.getRequestURI() + "" + response.userId()))
                .body(response);
    }

    //@GetMapping("/fill")
    public Long fill(){
        //return 0L;
        var context = new AnnotationConfigApplicationContext(BookingApplication.class);
        CarRepository r = context.getBean(CarRepository.class);
        SeatRepository r1= context.getBean(SeatRepository.class);

        SeatEntity [] seats =(SeatEntity [])TransportUtil.preFill().get("seats");
        CarEntity []  cars =(CarEntity[])TransportUtil.preFill().get("cars");

        for(SeatEntity s : seats){
            logger.info("Saving seat with id " + s.getCarId());
            r1.save(s);
            logger.info("Seat count " + r1.count());
        }
        for(CarEntity c : cars){
            logger.info("Saving car with id " + c.getCarId());
            r.save(c);
            logger.info("Car count " + r.count());
        }

        //context.close();

        return (Long)r1.count()+r.count();
    }
}