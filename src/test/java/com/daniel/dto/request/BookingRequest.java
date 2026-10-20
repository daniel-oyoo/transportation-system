package com.daniel.dto.request;

import java.math.BigDecimal;
import java.util.Date;

public record BookingRequest(
    String userId,
    String carId,
    String seatId,
    BigDecimal price,
    String route,
    String from,
    String to,
    Date travelDate
){}
