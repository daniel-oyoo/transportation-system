package com.daniel.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripResponse(
        Long id,
        String routeName,
        String origin,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal fare,
        String currency,
        int availableSeats) {
}
