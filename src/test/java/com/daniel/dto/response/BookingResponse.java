package com.daniel.dto.response;

import java.util.Date;

public record BookingResponse(
    String userId,
    String carId,
    String seatId,
    String route,
    String from,
    String to,
    Date travelDate
){}