package com.daniel.dto.response;

import java.time.LocalDate;

public record BookingResponse(
    String userId,
    String carId,
    Long seatId,
    String route,
    String from,
    String to,
    LocalDate travelDate
){}