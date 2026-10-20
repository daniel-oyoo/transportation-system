package com.daniel.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.daniel.dto.response.ErrorResponse;
import com.daniel.exception.SeatAlreadyBookedException;
import com.daniel.exception.SeatNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse>
      seatAlreadyBookedExceptionHandler(SeatAlreadyBookedException e){
      ErrorResponse response = new ErrorResponse(
        LocalDate.now().toString(),
        409,
        e.getMessage()
      );

      return 
         ResponseEntity
            .status(409)
            .body(response);
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse>
      SeatNotFoundExceptionHandler(SeatNotFoundException e){
      ErrorResponse response = new ErrorResponse(
        LocalDate.now().toString(),
        404,
        e.getMessage()
      );

      return 
         ResponseEntity
            .status(404)
            .body(response);
    }
}
