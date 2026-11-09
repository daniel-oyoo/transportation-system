package com.daniel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TripBookingRequest(
        @NotNull Long tripId,
        @NotNull Long seatId,
        @NotBlank @Size(max = 120) String passengerName,
        @NotBlank @Pattern(regexp = "^(?:\\+254|0)?[17][0-9]{8}$",
                message = "Enter a valid Kenyan mobile number") String passengerPhone) {
}
