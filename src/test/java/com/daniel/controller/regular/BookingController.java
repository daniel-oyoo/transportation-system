package com.daniel.controller.regular;

import java.net.URI;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.daniel.dto.request.BookingRequest;
import com.daniel.dto.response.BookingResponse;
import com.daniel.entity.SeatEntity;
import com.daniel.repository.SeatRepository;
import com.daniel.service.regular.CustomerService;

import jakarta.persistence.Column;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/booking")
public class BookingController {
    private final CustomerService customerService;

    private final Logger logger = Logger.getLogger(BookingController.class.getClass().getName());


    @Autowired
    public BookingController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> book(@Valid @RequestBody BookingRequest request) {
        logger.info("Received request to book travel {} " + request);
        
        BookingResponse response = customerService.book(request);
        
        return ResponseEntity
                .status(201)
                .location(URI.create("/api/v1/booking/" + response.userId()))
                .body(response);
    }
}