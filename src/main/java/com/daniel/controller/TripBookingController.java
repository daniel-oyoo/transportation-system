package com.daniel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.daniel.dto.request.TripBookingRequest;
import com.daniel.dto.response.TripBookingResponse;
import com.daniel.service.TravelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Trip bookings", description = "Create and look up passenger trip bookings")
public class TripBookingController {

    private final TravelService travelService;

    public TripBookingController(TravelService travelService) {
        this.travelService = travelService;
    }

    @PostMapping
    @Operation(summary = "Book one seat on a scheduled trip")
    public ResponseEntity<TripBookingResponse> book(@Valid @RequestBody TripBookingRequest request) {
        TripBookingResponse booking = travelService.book(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{reference}").buildAndExpand(booking.bookingReference()).toUri()).body(booking);
    }

    @GetMapping("/{reference}")
    @Operation(summary = "Look up a booking using its reference")
    public TripBookingResponse find(@PathVariable String reference) {
        return travelService.findBooking(reference);
    }
}
