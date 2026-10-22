package com.daniel.controller.regular;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.dto.request.BookingRequest;
import com.daniel.dto.response.BookingResponse;
import com.daniel.entity.CarEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.repository.CarRepository;
import com.daniel.repository.SeatRepository;
import com.daniel.service.DataSeederService;
import com.daniel.service.regular.CustomerService;
import com.daniel.util.TransportUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/booking")
@Tag(name = "Booking Management", description = "Endpoints for handling customer seat bookings and transport data initialization")
public class BookingController {

    private static final Logger LOGGER = Logger.getLogger(BookingController.class.getName());

    private final CustomerService customerService;
    private final DataSeederService dataSeederService;

    public BookingController(CustomerService customerService, DataSeederService dataSeederService) {
        this.customerService = customerService;
        this.dataSeederService = dataSeederService;
    }

    @Operation(summary = "Book a travel seat", description = "Validates car existence, checks seat availability, locks the seat, and records the booking.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Seat successfully booked"),
        @ApiResponse(responseCode = "404", description = "Car or Seat not found"),
        @ApiResponse(responseCode = "409", description = "Seat is already booked"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<BookingResponse> book(
            @Valid @RequestBody BookingRequest request,
            HttpServletRequest req
    ) {
        LOGGER.info(() -> "Received request to book travel for request: " + request);

        BookingResponse response = customerService.book(request);
        URI location = URI.create(req.getRequestURI() + "/" + response.userId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .body(response);
    }

    @Operation(summary = "Seed transport data", description = "Manually triggers generation and storage of default cars and seats into the database.")
    @ApiResponse(responseCode = "200", description = "Data seeded successfully with total count returned")
    @GetMapping("/fill")
    @PostMapping("/fill")
    public ResponseEntity<Map<String, String>> fillDataAsync() {
        // Triggers the background task on the custom thread pool
        dataSeederService.seedDataAsync();

        // Immediately return 202 Accepted to the client
        Map<String, String> response = Map.of(
                "status", "ACCEPTED",
                "message", "Data seeding task initiated asynchronously in the background."
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}