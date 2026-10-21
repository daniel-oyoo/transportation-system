package com.daniel.service.regular;

import com.daniel.dto.request.BookingRequest;
import com.daniel.dto.response.BookingResponse;
import com.daniel.entity.BookingEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.exception.SeatAlreadyBookedException;
import com.daniel.exception.SeatNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.daniel.repository.*;

@Service
public class CustomerService {

    private final BookingRepository bookingRepository;

    private final SeatRepository seatRepository;

    private final CarRepository carRepository;

    private final Logger logger = Logger.getLogger(CustomerService.class.getName());

    @Autowired
    public CustomerService(
      BookingRepository bookingRepository,
      SeatRepository seatRepository,
      CarRepository carRepository
    ) {
        this.carRepository=carRepository;
        this.bookingRepository = bookingRepository;
        this.seatRepository=seatRepository;
    }

    @Transactional
    public BookingResponse book(BookingRequest request) {

      logger.info("Received a booking request by user {}" + request.userId());

      //does the car even exists
       if(!carRepository.existsByCarId(request.carId())) throw new RuntimeException("No such car exists");
      //check for seats availability
      SeatEntity seat = seatRepository
                        //.findByCarId(request.carId())
                        .findById(request.seatId())
                        .orElseThrow(
                          ()->new SeatNotFoundException("Seat not found with id " +
                                                         request.seatId()
                                                        )
                        );
      logger.info("Checking seat availabilty....");
      //if seat unavaialbale throw exception
      if(!seat.isAvailable()){
       throw new SeatAlreadyBookedException("Seat with id " +  seat.getId() + " already booked");
      }

      //convert request to seat
      logger.info("booking a seat...");
      //seat
     // if(!carRepository.existsByCarId(request.carId())) throw new RuntimeException("No such car exists");
      seat.setAvailable(false);
      //seat.setCarId(request.carId());
      seatRepository.save(seat);
      logger.info("Seat booked successfully for user {} " + request.userId());
      //convert request to booking
      BookingEntity booking = toEntity(request);
      BookingEntity saved = bookingRepository.save(booking);
      return 
        toResponse(saved);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByUser(String userId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getUserId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BookingEntity toEntity(BookingRequest request){
      return new BookingEntity(
     request.userId(),
     request.carId(),
     request.seatId(),
     request.price(),
     request.route(),
     request.from(),
     request.to(),
     request.travelDate()
      );
    }

    private BookingResponse toResponse(BookingEntity entity) {
      return new BookingResponse(
    entity.getUserId(),
    entity.getCarId(),
    entity.getSeatId(),
    entity.getRoute(),
    entity.getSource(),
    entity.getDestination(),
    entity.getTravelDate()
      );
    }
}