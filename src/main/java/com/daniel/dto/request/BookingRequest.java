package com.daniel.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingRequest(
    String userId,
    String carId,
    Long seatId,
    BigDecimal price,
    String route,
    String from,
    String to,
    LocalDate travelDate
){}
