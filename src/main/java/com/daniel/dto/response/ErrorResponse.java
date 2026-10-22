package com.daniel.dto.response;

public record ErrorResponse(
    String timestamp,
    int status,
    String message
) {}
