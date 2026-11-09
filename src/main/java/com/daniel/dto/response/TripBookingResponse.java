package com.daniel.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripBookingResponse(
        String bookingReference,
        String passengerName,
        String origin,
        String destination,
        String carId,
        Long seatId,
        LocalDateTime departureTime,
        BigDecimal amount,
        String currency,
        String status,
        String paymentStatus) {
}
