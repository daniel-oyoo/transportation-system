package com.daniel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.dto.response.SeatAvailabilityResponse;
import com.daniel.dto.response.TripResponse;
import com.daniel.service.TravelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/trips")
@Tag(name = "Trips", description = "Search scheduled trips and available seats")
public class TripController {

    private final TravelService travelService;

    public TripController(TravelService travelService) {
        this.travelService = travelService;
    }

    @GetMapping
    @Operation(summary = "Search scheduled trips by route and travel date")
    public List<TripResponse> search(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return travelService.searchTrips(from, to, date);
    }

    @GetMapping("/{tripId}/seats")
    @Operation(summary = "List seat availability for a trip")
    public List<SeatAvailabilityResponse> seats(@PathVariable Long tripId) {
        return travelService.getSeats(tripId);
    }
}
